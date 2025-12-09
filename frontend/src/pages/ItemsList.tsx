import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import type { Item, ItemStatus } from '../api/types'

export default function ItemsList() {
  const [items, setItems] = useState<Item[] | null>(null)
  const [status, setStatus] = useState<ItemStatus | 'ALL'>('ALL')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [sortKey, setSortKey] = useState<'id' | 'name' | 'status' | 'createdAt' | 'lastModifiedAt'>('id')
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc')
  const navigate = useNavigate()

  const fetchData = async (s?: ItemStatus) => {
    setLoading(true)
    setError(null)
    try {
      const data = await api.listItems(s)
      setItems(data)
    } catch (e: any) {
      setError(e?.message ?? 'Failed to load items')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData(status === 'ALL' ? undefined : status)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status])

  const onDelete = async (id: number) => {
    if (!confirm(`Delete item ${id}?`)) return
    try {
      await api.deleteItem(id)
      await fetchData(status === 'ALL' ? undefined : status)
    } catch (e: any) {
      alert(e?.message ?? 'Delete failed')
    }
  }

  const onToggleStatus = async (item: Item) => {
    const next: ItemStatus = item.status === 'CURRENT' ? 'DISCONTINUED' : 'CURRENT'
    try {
      await api.updateItemStatus(item.id, next)
      await fetchData(status === 'ALL' ? undefined : status)
    } catch (e: any) {
      alert(e?.message ?? 'Status update failed')
    }
  }

  const filtered = useMemo(() => items ?? [], [items])

  const sorted = useMemo(() => {
    const data = [...filtered]
    const dir = sortDir === 'asc' ? 1 : -1
    const statusOrder: Record<ItemStatus, number> = { CURRENT: 1, DISCONTINUED: 2 }
    data.sort((a, b) => {
      switch (sortKey) {
        case 'id':
          return (a.id - b.id) * dir
        case 'name': {
          const an = a.name.toLocaleLowerCase()
          const bn = b.name.toLocaleLowerCase()
          if (an < bn) return -1 * dir
          if (an > bn) return 1 * dir
          return 0
        }
        case 'status':
          return (statusOrder[a.status] - statusOrder[b.status]) * dir
        case 'createdAt':
          return (new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()) * dir
        case 'lastModifiedAt':
          return (new Date(a.lastModifiedAt).getTime() - new Date(b.lastModifiedAt).getTime()) * dir
        default:
          return 0
      }
    })
    return data
  }, [filtered, sortKey, sortDir])

  const toggleSort = (key: typeof sortKey) => {
    if (sortKey === key) {
      setSortDir(d => (d === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortKey(key)
      setSortDir('asc')
    }
  }

  const sortIndicator = (key: typeof sortKey) => sortKey === key ? (sortDir === 'asc' ? ' ▲' : ' ▼') : ''

  return (
    <section>
      <div className="toolbar">
        <label>
          Status:
          <select value={status} onChange={e => setStatus(e.target.value as any)}>
            <option value="ALL">All</option>
            <option value="CURRENT">CURRENT</option>
            <option value="DISCONTINUED">DISCONTINUED</option>
          </select>
        </label>
        <button className="primary" onClick={() => navigate('/items/new')}>New Item</button>
      </div>

      {loading && <p>Loading…</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        <table className="table">
          <thead>
            <tr>
              <th className="sortable" onClick={() => toggleSort('id')}>ID{sortIndicator('id')}</th>
              <th className="sortable" onClick={() => toggleSort('name')}>Name{sortIndicator('name')}</th>
              <th className="sortable" onClick={() => toggleSort('status')}>Status{sortIndicator('status')}</th>
              <th className="sortable" onClick={() => toggleSort('createdAt')}>Created{sortIndicator('createdAt')}</th>
              <th className="sortable" onClick={() => toggleSort('lastModifiedAt')}>Updated{sortIndicator('lastModifiedAt')}</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {sorted.map(it => (
              <tr key={it.id}>
                <td>{it.id}</td>
                <td><Link to={`/items/${it.id}`}>{it.name}</Link></td>
                <td>
                  <span className={`badge ${it.status.toLowerCase()}`}>{it.status}</span>
                </td>
                <td>{new Date(it.createdAt).toLocaleString()}</td>
                <td>{new Date(it.lastModifiedAt).toLocaleString()}</td>
                <td className="actions">
                  <button onClick={() => navigate(`/items/${it.id}`)}>View</button>
                  <button onClick={() => navigate(`/items/${it.id}/edit`)}>Edit</button>
                  <button onClick={() => onToggleStatus(it)}>
                    Set {it.status === 'CURRENT' ? 'DISCONTINUED' : 'CURRENT'}
                  </button>
                  <button className="danger" onClick={() => onDelete(it.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
