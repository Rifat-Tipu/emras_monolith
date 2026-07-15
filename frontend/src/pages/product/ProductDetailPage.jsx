import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ShoppingBag, ArrowLeft, Check } from 'lucide-react'
import { getProductBySlugApi } from '../../api/productApi'
import Spinner from '../../components/common/Spinner'

const ProductDetailPage = () => {
  const { slug } = useParams()

  const [product,         setProduct]         = useState(null)
  const [loading,         setLoading]         = useState(true)
  const [selectedVariant, setSelectedVariant] = useState(null)
  const [selectedImage,   setSelectedImage]   = useState(null)

  useEffect(() => {
    setLoading(true)
    getProductBySlugApi(slug)
      .then(res => {
        const p = res.data.data
        setProduct(p)
        // default to first active variant
        const firstActive = p.variants?.find(v => v.active)
        setSelectedVariant(firstActive || null)
        // default to primary image
        const primary = p.images?.find(img => img.primary) || p.images?.[0]
        setSelectedImage(primary?.imageUrl || null)
      })
      .catch(() => setProduct(null))
      .finally(() => setLoading(false))
  }, [slug])

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Spinner size="lg" />
      </div>
    )
  }

  if (!product) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4">
        <p className="text-gray-500">Product not found.</p>
        <Link to="/products" className="text-sm text-black font-medium hover:underline">
          Back to shop
        </Link>
      </div>
    )
  }

  const displayPrice = selectedVariant
    ? selectedVariant.effectivePrice
    : (product.discountPrice || product.price)

  const hasDiscount  = !!product.discountPrice
  const isInStock    = selectedVariant?.inStock ?? false

  // Group variants by size
  const sizes  = [...new Set(product.variants?.map(v => v.size))]
  const colors = [...new Set(product.variants?.map(v => v.color).filter(Boolean))]

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">

      {/* Back link */}
      <Link
        to="/products"
        className="inline-flex items-center gap-1.5 text-sm text-gray-500
                   hover:text-gray-900 transition-colors mb-6"
      >
        <ArrowLeft className="w-4 h-4" />
        Back to shop
      </Link>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">

        {/* ── Left: Images ─────────────────────────────────────────────── */}
        <div className="space-y-3">
          {/* Main image */}
          <motion.div
            key={selectedImage}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="aspect-[4/5] bg-gray-50 rounded-2xl overflow-hidden"
          >
            {selectedImage ? (
              <img
                src={selectedImage}
                alt={product.name}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center">
                <ShoppingBag className="w-20 h-20 text-gray-200" />
              </div>
            )}
          </motion.div>

          {/* Thumbnail row */}
          {product.images?.length > 1 && (
            <div className="flex gap-2">
              {product.images.map(img => (
                <button
                  key={img.id}
                  onClick={() => setSelectedImage(img.imageUrl)}
                  className={`w-16 h-16 rounded-lg overflow-hidden border-2 transition-colors
                    ${selectedImage === img.imageUrl
                      ? 'border-black'
                      : 'border-transparent hover:border-gray-300'}`}
                >
                  <img
                    src={img.imageUrl}
                    alt={img.altText || product.name}
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* ── Right: Info ───────────────────────────────────────────────── */}
        <div className="space-y-6">

          {/* Category + name */}
          <div>
            {product.categoryName && (
              <p className="text-xs text-gray-400 uppercase tracking-widest mb-2">
                {product.categoryName}
              </p>
            )}
            <h1 className="text-2xl font-bold text-gray-900">{product.name}</h1>
            {product.brand && (
              <p className="text-sm text-gray-500 mt-1">by {product.brand}</p>
            )}
          </div>

          {/* Price */}
          <div className="flex items-center gap-3">
            <span className="text-2xl font-bold text-gray-900">
              ৳{displayPrice?.toLocaleString()}
            </span>
            {hasDiscount && (
              <span className="text-base text-gray-400 line-through">
                ৳{product.price?.toLocaleString()}
              </span>
            )}
            {hasDiscount && (
              <span className="bg-black text-white text-xs px-2 py-0.5 rounded-full">
                Sale
              </span>
            )}
          </div>

          {/* Description */}
          {product.description && (
            <p className="text-sm text-gray-600 leading-relaxed">
              {product.description}
            </p>
          )}

          {/* Color selector */}
          {colors.length > 0 && (
            <div>
              <p className="text-sm font-medium text-gray-700 mb-2">Color</p>
              <div className="flex gap-2">
                {colors.map(color => (
                  <button
                    key={color}
                    onClick={() => {
                      const v = product.variants.find(
                        vr => vr.color === color &&
                              vr.size === selectedVariant?.size &&
                              vr.active
                      ) || product.variants.find(vr => vr.color === color)
                      if (v) setSelectedVariant(v)
                    }}
                    className={`px-3 py-1.5 rounded-lg border text-sm transition-colors
                      ${selectedVariant?.color === color
                        ? 'border-black bg-black text-white'
                        : 'border-gray-200 text-gray-700 hover:border-black'}`}
                  >
                    {color}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Size selector */}
          {sizes.length > 0 && (
            <div>
              <p className="text-sm font-medium text-gray-700 mb-2">Size</p>
              <div className="flex flex-wrap gap-2">
                {sizes.map(size => {
                  const variant = product.variants.find(
                    v => v.size === size &&
                         (colors.length === 0 || v.color === selectedVariant?.color)
                  )
                  const outOfStock = variant && !variant.inStock
                  const isSelected = selectedVariant?.size === size

                  return (
                    <button
                      key={size}
                      onClick={() => variant && setSelectedVariant(variant)}
                      disabled={outOfStock}
                      className={`w-12 h-12 rounded-xl border text-sm font-medium transition-colors
                        ${isSelected
                          ? 'border-black bg-black text-white'
                          : outOfStock
                            ? 'border-gray-100 text-gray-300 cursor-not-allowed line-through'
                            : 'border-gray-200 text-gray-700 hover:border-black'}`}
                    >
                      {size}
                    </button>
                  )
                })}
              </div>
            </div>
          )}

          {/* Stock status */}
          <div className="flex items-center gap-1.5">
            {isInStock ? (
              <>
                <Check className="w-4 h-4 text-green-500" />
                <span className="text-sm text-green-600">
                  In stock ({selectedVariant?.stockQuantity} available)
                </span>
              </>
            ) : (
              <span className="text-sm text-red-500">Out of stock</span>
            )}
          </div>

          {/* Add to cart button — wired in Phase 2 */}
          <motion.button
            whileTap={{ scale: 0.98 }}
            disabled={!isInStock}
            className="w-full flex items-center justify-center gap-2
                       bg-black text-white py-3.5 rounded-xl font-medium
                       hover:bg-gray-800 transition-colors
                       disabled:opacity-40 disabled:cursor-not-allowed"
          >
            <ShoppingBag className="w-4 h-4" />
            {isInStock ? 'Add to Cart' : 'Out of Stock'}
          </motion.button>

          {/* Tags */}
          {product.tags && (
            <div className="flex flex-wrap gap-2 pt-2">
              {product.tags.split(',').map(tag => (
                <span
                  key={tag}
                  className="text-xs text-gray-400 bg-gray-50 px-2.5 py-1 rounded-full"
                >
                  {tag.trim()}
                </span>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default ProductDetailPage