export type ItemStatus = 'CURRENT' | 'DISCONTINUED'

export interface Item {
  id: number
  status: ItemStatus
  name: string
  summary: string
  createdAt: string
  lastModifiedAt: string
  discontinuedAt?: string | null
}

export interface ItemDto {
  name: string
  status: ItemStatus
  description: string
}
