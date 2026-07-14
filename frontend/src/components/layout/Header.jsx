import { Link } from 'react-router-dom'
import useAuthStore from '../../store/authStore'
import useCartStore from '../../store/cartStore'

const Header = () => {
  const { isLoggedIn, user } = useAuthStore()
  const { itemCount }        = useCartStore()

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <Link to="/" className="text-2xl font-bold tracking-tight text-gray-900">
        EMRAS
      </Link>

      <nav className="flex items-center gap-6 text-sm font-medium text-gray-600">
        <Link to="/products" className="hover:text-gray-900 transition-colors">
          Shop
        </Link>

        {isLoggedIn ? (
          <>
            <span className="text-gray-900">Hi, {user?.fullName}</span>
            <Link to="/cart" className="relative hover:text-gray-900">
              Cart
              {itemCount > 0 && (
                <span className="absolute -top-2 -right-3 bg-black text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
                  {itemCount}
                </span>
              )}
            </Link>
          </>
        ) : (
          <>
            <Link to="/login"    className="hover:text-gray-900 transition-colors">Login</Link>
            <Link to="/register" className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors">
              Register
            </Link>
          </>
        )}
      </nav>
    </header>
  )
}

export default Header