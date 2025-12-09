import { FormEvent, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import type { Item, ItemDto, ItemStatus } from '../api/types'

export default function ItemForm({ mode }: { mode: 'create' | 'edit' }) {
  const navigate = useNavigate()
  const { id } = useParams()
  const itemId = Number(id)

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [status, setStatus] = useState<ItemStatus>('CURRENT')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (mode === 'edit' && Number.isFinite(itemId)) {
      setLoading(true)
      api.getItem(itemId)
        .then((it: Item) => {
          setName(it.name)
          setDescription(it.summary)
          setStatus(it.status)
        })
        .catch((e: any) => setError(e?.message ?? 'Failed to load item'))
        .finally(() => setLoading(false))
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mode, itemId])

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    if (!name.trim() || !description.trim()) {
      setError('Name and description are required')
      return
    }
    const dto: ItemDto = { name: name.trim(), description: description.trim(), status }
    try {
      setLoading(true)
      if (mode === 'create') {
        const created = await api.createItem(dto)
        navigate(`/items/${created.id}`)
      } else {
        await api.updateItem(itemId, dto)
        navigate(`/items/${itemId}`)
      }
    } catch (e: any) {
      setError(e?.message ?? 'Save failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section>
      <h2>{mode === 'create' ? 'Create Item' : `Edit Item #${itemId}`}</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={onSubmit} className="form">
        <label>
          <span>Name</span>
          <input value={name} onChange={e => setName(e.target.value)} placeholder="Item name" />
        </label>
        <label>
          <span>Description</span>
          <textarea value={description} onChange={e => setDescription(e.target.value)} placeholder="Short description" />
        </label>
        <label>
          <span>Status</span>
          <select value={status} onChange={e => setStatus(e.target.value as ItemStatus)}>
            <option value="CURRENT">CURRENT</option>
            <option value="DISCONTINUED">DISCONTINUED</option>
          </select>
        </label>
        <div className="actions">
          <button type="button" onClick={() => navigate(-1)}>Cancel</button>
          <button className="primary" type="submit" disabled={loading}>
            {loading ? 'Saving…' : 'Save'}
          </button>
        </div>
      </form>
    </section>
  )
}
