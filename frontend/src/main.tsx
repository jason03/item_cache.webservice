import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App'
import './styles.css'

const rootEl = document.getElementById('root')!

// In React 18 StrictMode, effects run twice in development which can cause
// duplicate API calls. Render without StrictMode in dev to avoid this.
const appTree = (
  <BrowserRouter>
    <App />
  </BrowserRouter>
)

const rootContent = import.meta.env.DEV ? appTree : (
  <React.StrictMode>{appTree}</React.StrictMode>
)

createRoot(rootEl).render(rootContent)
