import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft } from 'lucide-react'

const NotFoundPage = () => (
  <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      className="text-center"
    >
      <p className="text-8xl font-bold text-gray-100 mb-4">404</p>
      <h1 className="text-xl font-semibold text-gray-900 mb-2">
        Page not found
      </h1>
      <p className="text-gray-500 text-sm mb-8">
        The page you're looking for doesn't exist.
      </p>
      <Link
        to="/"
        className="inline-flex items-center gap-2 bg-black text-white
                   px-5 py-2.5 rounded-xl text-sm font-medium
                   hover:bg-gray-800 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Back to home
      </Link>
    </motion.div>
  </div>
)

export default NotFoundPage