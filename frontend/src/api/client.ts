import type { Item, ItemDto, ItemStatus } from './types'

const API_BASE = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

async function handle<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let message = `HTTP ${res.status}`
    try {
      const data = await res.json()
      if (data?.message) message = data.message
    } catch {}
    throw new Error(message)
  }
  // 204 = No Content
  if (res.status === 204) return undefined as unknown as T
  return res.json() as Promise<T>
}

export const api = {
  listItems: async (status?: ItemStatus): Promise<Item[]> => {
    const qs = status ? `?status=${encodeURIComponent(status)}` : ''
    const res = await fetch(`${API_BASE}/items${qs}`)
    return handle<Item[]>(res)
  },
  getItem: async (id: number): Promise<Item> => {
    const res = await fetch(`${API_BASE}/items/${id}`)
    return handle<Item>(res)
  },
  createItem: async (dto: ItemDto): Promise<Item> => {
    const res = await fetch(`${API_BASE}/items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto)
    })
    return handle<Item>(res)
  },
  updateItem: async (id: number, dto: ItemDto): Promise<void> => {
    const res = await fetch(`${API_BASE}/items/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto)
    })
    return handle<void>(res)
  },
  updateItemStatus: async (id: number, status: ItemStatus): Promise<void> => {
    const res = await fetch(`${API_BASE}/items/${id}?status=${encodeURIComponent(status)}`, {
      method: 'PUT'
    })
    return handle<void>(res)
  },
  deleteItem: async (id: number): Promise<void> => {
    const res = await fetch(`${API_BASE}/items/${id}`, { method: 'DELETE' })
    return handle<void>(res)
  }
}

export const config = { API_BASE }
