import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import useAuthStore from '../../store/authStore'
import useCartStore from '../../store/cartStore'
import { logoutApi } from '../../api/authApi'

const Header = () => {
  const navigate              = useNavigate()
  const { isLoggedIn, user, clearAuth } = useAuthStore()
  const { itemCount, clearCart }        = useCartStore()

  const handleLogout = async () => {
    try {
      await logoutApi()
    } catch {
      // silently ignore — clear client state regardless
    } finally {
      clearAuth()
      clearCart()
      toast.success('Logged out successfully')
      navigate('/login')
    }
  }

  return (
    <header className="bg-white border-b border-gray-100 px-6 py-4 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto flex items-center justify-between">

        {/* Logo */}
        <Link to="/" className="text-xl font-bold tracking-widest text-gray-900">
          EMRAS
        </Link>

        {/* Nav */}
        <nav className="flex items-center gap-6 text-sm font-medium text-gray-600">
          <Link
            to="/products"
            className="hover:text-gray-900 transition-colors"
          >
            Shop
          </Link>

          {isLoggedIn ? (
            <>
              <Link to="/cart" className="relative hover:text-gray-900 transition-colors">
                Cart
                {itemCount > 0 && (
                  <motion.span
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    className="absolute -top-2 -right-3 bg-black text-white
                               text-xs rounded-full w-4 h-4 flex items-center justify-center"
                  >
                    {itemCount}
                  </motion.span>
                )}
              </Link>

              <span className="text-gray-400">|</span>

              <span className="text-gray-700">
                Hi, {user?.fullName?.split(' ')[0]}
              </span>

              <button
                onClick={handleLogout}
                className="text-gray-500 hover:text-gray-900 transition-colors"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="hover:text-gray-900 transition-colors"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="bg-black text-white px-4 py-2 rounded-lg
                           hover:bg-gray-800 transition-colors"
              >
                Register
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}

export default Header