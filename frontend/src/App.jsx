import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import HomePage    from './pages/home/HomePage'
import LoginPage   from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import ProductListPage from './pages/product/ProductListPage'
import ProductDetailPage from './pages/product/ProductDetailPage'
import NotFoundPage from './pages/error/NotFoundPage'
import Header from './components/layout/Header'

function App() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" />
      <Header />
      <main>
        <Routes>
          <Route path="/"               element={<HomePage />} />
          <Route path="/login"          element={<LoginPage />} />
          <Route path="/register"       element={<RegisterPage />} />
          <Route path="/products"       element={<ProductListPage />} />
          <Route path="/products/:slug" element={<ProductDetailPage />} />
          <Route path="*"               element={<NotFoundPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}

export default App