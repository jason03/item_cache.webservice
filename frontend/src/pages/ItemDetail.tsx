import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import type { Item, ItemStatus } from '../api/types'

export default function ItemDetail() {
  const { id } = useParams()
  const itemId = Number(id)
  const [item, setItem] = useState<Item | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const load = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await api.getItem(itemId)
      setItem(data)
    } catch (e: any) {
      setError(e?.message ?? 'Failed to load item')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!Number.isFinite(itemId)) return
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [itemId])

  const onDelete = async () => {
    if (!confirm(`Delete item ${itemId}?`)) return
    try {
      await api.deleteItem(itemId)
      navigate('/items')
    } catch (e: any) {
      alert(e?.message ?? 'Delete failed')
    }
  }

  const onToggleStatus = async () => {
    if (!item) return
    const next: ItemStatus = item.status === 'CURRENT' ? 'DISCONTINUED' : 'CURRENT'
    try {
      await api.updateItemStatus(item.id, next)
      await load()
    } catch (e: any) {
      alert(e?.message ?? 'Status update failed')
    }
  }

  if (!Number.isFinite(itemId)) return <p>Invalid item id</p>

  return (
    <section>
      <div className="toolbar">
        <button onClick={() => navigate(-1)}>Back</button>
        <Link to={`/items/${itemId}/edit`} className="primary">Edit</Link>
        <button onClick={onToggleStatus}>Toggle Status</button>
        <button className="danger" onClick={onDelete}>Delete</button>
      </div>

      {loading && <p>Loading…</p>}
      {error && <p className="error">{error}</p>}

      {item && (
        <div className="card">
          <h2>
            {item.name} <span className={`badge ${item.status.toLowerCase()}`}>{item.status}</span>
          </h2>
          <p><strong>ID:</strong> {item.id}</p>
          <p><strong>Description:</strong> {item.summary}</p>
          <p><strong>Created:</strong> {new Date(item.createdAt).toLocaleString()}</p>
          <p><strong>Last Modified:</strong> {new Date(item.lastModifiedAt).toLocaleString()}</p>
          {item.discontinuedAt && <p><strong>Discontinued:</strong> {new Date(item.discontinuedAt).toLocaleString()}</p>}
        </div>
      )}
    </section>
  )
}
