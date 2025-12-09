import { Link, Navigate, Route, Routes } from 'react-router-dom'
import ItemsList from './pages/ItemsList'
import ItemDetail from './pages/ItemDetail'
import ItemForm from './pages/ItemForm'

export default function App() {
  return (
    <div className="container">
      <header className="header">
        <h1>Item Cache UI</h1>
        <nav>
          <Link to="/items">Items</Link>
          <Link to="/items/new" className="primary">New Item</Link>
        </nav>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<Navigate to="/items" replace />} />
          <Route path="/items" element={<ItemsList />} />
          <Route path="/items/new" element={<ItemForm mode="create" />} />
          <Route path="/items/:id" element={<ItemDetail />} />
          <Route path="/items/:id/edit" element={<ItemForm mode="edit" />} />
          <Route path="*" element={<p>Not found</p>} />
        </Routes>
      </main>
    </div>
  )
}
