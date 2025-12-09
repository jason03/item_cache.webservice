### Frontend (React) – Item Cache UI

We provide a simple React + TypeScript UI (Vite) in `frontend/` to interact with the API.

#### Prerequisites
- Node.js 18+ (recommended 20+)

#### Dev server
1. Start the backend on port 8080.
2. In another terminal:
```
cd frontend
npm install
npm run dev
```
3. Open http://localhost:5173

The backend is CORS-enabled for `http://localhost:5173` on `/api/v1/**` routes.

#### API base URL
- Default (dev): `http://localhost:8080/api/v1`
- To override, set an env var for Vite before starting:
```
VITE_API_BASE_URL="http://localhost:8080/api/v1" npm run dev
```

#### Available pages and actions
- Items list: view all items, filter by status (All/CURRENT/DISCONTINUED), navigate to details, edit, toggle status, delete.
- Item details: view item, toggle status, delete, edit.
- New item: create item with `name`, `description`, and `status`.

Routes:
- `/items` – list
- `/items/new` – create
- `/items/:id` – detail
- `/items/:id/edit` – edit

#### Building for production
```
cd frontend
npm install
npm run build
```
This produces static files in `frontend/dist/`. You can serve them with any static server or copy under the backend's static resources if you wish to embed the UI in the Spring Boot app (e.g., `src/main/resources/static`).

---


### Frontend troubleshooting notes

- Duplicate API calls in dev:
    - React 18 StrictMode intentionally mounts components twice in development to surface side‑effects. This causes `useEffect` blocks to run twice and can look like duplicate GET requests in the Network tab.
    - We render without StrictMode during development to avoid duplicate GETs; production builds still use StrictMode. If you prefer to keep StrictMode in dev, you may see two GETs per page load — this is expected behavior in dev only.

- OPTIONS requests before mutations (CORS preflight):
    - For `POST`, `PUT`, `DELETE` with `Content-Type: application/json`, the browser will send an `OPTIONS` preflight request followed by the actual request. Seeing one `OPTIONS` and one `POST/PUT/DELETE` is expected.
    - Simple `GET` requests do not require a preflight and should appear once after the change above.


