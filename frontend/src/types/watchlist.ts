export interface WatchlistItem {
  itemId: number;
  itemName: string;
  icon: string;
  high: number | null;
  low: number | null;
  margin: number | null;
  flipScore: number | null;
  addedAt: string;
}
