## ADR-00x:
**Date:**  **Status:** 

**Context:**

**Options:**

**Decision:**

**Consequences:**

--- BOILER PLATE FOR COPY PASTE ABOVE ---

## ADR-001: Price data source and polling etiquette
**Date:** 2026-09-23 **Status:** Accepted

**Context:** Required data: prices, volume and buy limits for ~4000 items, refreshed every few minutes at no cost.
**Options:** The OSRS Wiki Real-time Prices API (180 days of past recorded data with the ability to pull data in 5 minutes or 1 hour increments) and the RuneScape official site's Grand Exchange page which only updates daily.  
**Decision:** The OSRS Wiki Real-time Prices API. Prices, volume and buy limit all in the one place and update every 5 minutes. The official site lacks support and only updates once a day leading to less accurate price watching.
**Consequences:** There are a few rules to follow. 1. The default Python User-Agents are blocked so the header has to be set explicitly with contact information coming from config. 2. Ping within reason in order to not threaten the stability of the entire API. e.g. One request that returns all ~4000 items is better than 4000 requests for one item each. And if the API goes down or has its format changed this app will silently go stale, please see ADR-004 for the recovery process. 

## ADR-002: Grand Exchange (GE) tax rules
**Date:** 2026-09-23 **Status:** Accepted

**Context:** There is a 2% tax per item rounded down with a 5Mgp limit, paid only by the seller. Data sourced from https://oldschool.runescape.wiki/w/Grand_Exchange on 2026-09-23. There is also a list of items exempt from the tax which is found from the same. During research I used AI and found it was wrong about five items, lesson learned.

**Options:** 1. Hard code in a list for the tax exempt items can check the sold item against this list before calculating the tax. The list will have to be kept updated but the data will be accurate and the list rarely ever changes. 2. An is_tax_exempt columnon the items table. This allows every process to read it and it can be queried but will need a migration or seed script to populate.

**Decision:** Going with option 2 above. The is_tax_exempt column can be easily checked on an item before calculating the tax. The API and the worker can both see whether an item is exempt and the front end will be able to display it. Tax calculation is as follows. 
    tax_per_item = 0 if is_tax_exempt else min(GE_TAX_CAP, (sale_price * GE_TAX_PERCENTAGE) // 100)
    total_ge_tax = tax_per_item * quantity_sold

**Consequences:** Although Jagex dont add items to the tax exempt list often it is still very important to ensure this list is kept up to date, if not then and items profit margin could be skewed. The seed script needs to be kept up to date with the bare minumum of the source URL being checked alongside game updates. If one of the names in the list matches zero items the seed script should log a warning and carry on. That way the application carries on with a warning that needs to be addressed. 

## ADR-003: Volume source and window
**Date:** 2026-09-23 **Status:** Accepted

**Context:** Volume is used for two things: working out how many of an item a user can realistically flip right now, and filtering out items that barely trade at all. 

**Options:** 1. 5 minute window. Too short, if a popular item has a quiet 5 minutes then it will drop off the table giving an inaccurate representation of volume. 2. A rolling 4 hour window. This one makes the most sense, gives a bigger sample size than the 5 minute window allowing for a more accurate representation of volume and also coincides with the 4 hour buy limit window. Its big enough to smooth out any noise from big spikes and drops but still small enough to reflect what is happening in the moment. 3. 24 hour window. This window is to big to measure "how much can I flip NOW" but is good to tell if the market is real at all and avoids one burst of trades giving a misleading number. The 24 hours window is great figure to use as a liquidity floor. 4. Hybrid; a rolling 4 hour window for quantity, plus a 24 hour window as a liquidity floor.

**Decision:** Going with option 4 above is going to be best suited to give the user the data they need. The rolling 4 hour window will be used to give the user the data they need in order to know if the item is trading well in the moment while the 24 hour window will be used to keep a quiet item that just had a massive single trade spike out of the top rankings and giving a false sense of a good flip. Volume is calculated by ading up the 5 minute blocks already stored so it takes no extra API calls.

**Consequences:** The hybrid model allows for the 4 hour window to react to real drops and spikes while the 24 hour window helps to ignore quiet items with once off spikes. The window is the last 4 hours of trading and is used to estimate the users next 4 hours. The liquidity floor number, "at least X trades per day", isn't decided yet. It'll be set in the ranking doc. Atleast 24 hours of raw 5 minute data must be kept. The first-run backfill of 24 hours is exactly what the floor needs. If part of it fails, the floor numbers are too low until the missing blocks are filled.

## ADR-004: Recovering from downtime
**Date:** 2026-09-23 **Status:** Accepted

**Context:** The OSRS Wiki API goes down, the worker crashes or a network drop. How do we recover from this.

**Options:** 1. Accept the gap and carry on from when the issue gets resolved. 2. Find the last stored block and backfill using the /5m timestamp parameter. If no blocks exist in the last 24 hours then only backfill the last 24 hours.

**Decision:** Going with option 2 above. If the API was only down for 20 minutes then the application only needs to fetch 4 5 minute blocks. As a fail safe if the API is down for an extended period of time i.e. longer than 24 hours, then the application will just default to backfilling the last 24 hours of data. Each item can only have one row per block, so fetching a block twice is harmless. If the worker is down for 3 days and only the last 24 hours is backfilled then a 2 day gap is left. There is good reason for this, the ranking only needs the last 24 hours to work accurately and its polite to the API. Should the Wiki be down for a long time the worker shouldnt hammer the API. Each time it tires and fails it waits longer before trying again, 1 minute, then 2, then 4 etc as it exponentially backs off. When the API returns each backfill request should be sent with a slight delay inbetween to avoid sending hundreds of requests at once.

**Consequences:** Back filling from the last known block of data allows for the application to get back up and running quicker, saving time by not backfilling data that it may have already had. In order to avoid duplication in the blocks the database will be given a rule stating that each primary key (item_id, block_timestamp) can only have 1 row per timestamp block. "If this row already exists, overwrite it" -> INSERT ... ON CONFLICT DO UPDATE. Outages for longer than 24 hours will leave gaps in the data. Rankings will go stake during an outage so display a "last updated" time for the user. Alerts must not fire on old data while its being backfilled. Open question: telling a block that was fetched but empty from one never fetched, to be solved in the data model.

## ADR-005: Items with unknown buy limits
**Date:** 2026-09-23 **Status:** Accepted

**Context:** There are some items on the GE with unknown buy limits, what do we do with them and how do we handle them.

**Options:** 1. Ignore this fact altogether, treat the buy limit as zero which hides it from the ranking list. 2. Treat them as having no buy limit, even though one may exist. This allows them to still show up on the ranking should they have the opportunity to. 3. Assume a default limit. Assign this items a limit being the median limit of similr items on the market. 

**Decision:** Going with option 2 above. Treating them as having no buy limit means the item is calculated purely based off of 4 hour volume which will allow for them to show up on the rankings if they happen break into it. 

**Consequences:** The risk in giving these items an unlimited buy limit is that a user may infact end up discovering one thus leading to a lower profit margin then the user was expecting. A label should be given to these items so that the user is aware "buy limit unknown" and should the user happen to discover the limit they are prompted to report it to the Wiki. The ranking's unit tests must include an item with a missing limit. Once ingestion is running, check the real volumes of unknown-limit items to see whether they're popular.

## ADR-006: Realistic flip quantity
**Date:** 2026-09-23 **Status:** Accepted

**Context:** How many of an item should the ranking assume a user can actually flip in the next 4 hours. Make sure the user gets a realistic number for this items flip, quantity multiplies the margin so overstating it overstates profit.

**Options:** Scenario: Potions have a buy limit of 1000. 500 traded at the low price (instant-sells) and only 2 at the high price (instant-buys). How many can the user realistically flip? 1. Just the buy limit. This is the naive approach many flipping tools make, basically tells the user they are capable of selling the full buy limit worth of potions at the high price. 2. Buy limit capped by total volume, min(1000, 500 + 2). 502 is not a realistic number to flip. 3. Buy limit capped by the average of the two sides, min(1000, (500 + 2) / 2) is 251 and still not a realistic number when only 2 were sold at the high price. 4. Buy limit capped by the smaller side, min(1000, 500, 2) = 2.

**Decision:** Going with option 4 above, realistic_quantity = min(buy_limit, low_volume_4h, high_volume_4h). Given that only 2 potions were sold at the high price in the last 4 hours this equation give the user the most accurate number of items they can realistically flip at the high price in the next 4 hour window. You can't complete more flips than the side with less volume allows.

**Consequences:** Unknown buy limits will become math.inf in one place, before ranking. The database will keep null and the API sends null, never infinity. If either sides volume is 0 then the quantity is also 0 so the item drops out of the ranking. One downside is that the rule is fairly pessimistic. The user may sell more than the recent volume suggests over a longer period of time. An open question for the ranking doc: you share that volume with other flippers, so you may only get part of it.