export interface PriceAlert {
  id: number;
  itemId: number;
  itemName: string;
  icon: string;
  targetMargin: number;
  currentMargin: number | null;
  triggered: boolean;
  dismissed: boolean;
  triggeredAt: string | null;
  createdAt: string;
}
