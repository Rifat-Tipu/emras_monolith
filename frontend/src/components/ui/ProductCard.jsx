import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ShoppingBag } from 'lucide-react'

const ProductCard = ({ product }) => {
  const displayPrice = product.discountPrice || product.price
  const hasDiscount  = !!product.discountPrice

  return (
    <motion.div
      whileHover={{ y: -4 }}
      transition={{ duration: 0.2 }}
      className="group bg-white rounded-2xl overflow-hidden border border-gray-100 hover:shadow-md transition-shadow"
    >
      {/* Image */}
      <Link to={`/products/${product.slug}`}>
        <div className="relative aspect-[3/4] bg-gray-50 overflow-hidden">
          {product.primaryImageUrl ? (
            <img
              src={product.primaryImageUrl}
              alt={product.name}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <ShoppingBag className="w-12 h-12 text-gray-200" />
            </div>
          )}

          {/* Discount badge */}
          {hasDiscount && (
            <span className="absolute top-3 left-3 bg-black text-white text-xs
                             font-medium px-2 py-1 rounded-full">
              Sale
            </span>
          )}

          {/* Out of stock overlay */}
          {product.variants?.every(v => !v.inStock) && (
            <div className="absolute inset-0 bg-white/70 flex items-center justify-center">
              <span className="text-sm font-medium text-gray-500">Out of Stock</span>
            </div>
          )}
        </div>
      </Link>

      {/* Info */}
      <div className="p-4">
        {product.categoryName && (
          <p className="text-xs text-gray-400 uppercase tracking-wider mb-1">
            {product.categoryName}
          </p>
        )}

        <Link to={`/products/${product.slug}`}>
          <h3 className="text-sm font-medium text-gray-900 hover:text-black
                         line-clamp-1 transition-colors">
            {product.name}
          </h3>
        </Link>

        <div className="flex items-center gap-2 mt-2">
          <span className="text-sm font-semibold text-gray-900">
            ৳{displayPrice?.toLocaleString()}
          </span>
          {hasDiscount && (
            <span className="text-xs text-gray-400 line-through">
              ৳{product.price?.toLocaleString()}
            </span>
          )}
        </div>
      </div>
    </motion.div>
  )
}

export default ProductCard