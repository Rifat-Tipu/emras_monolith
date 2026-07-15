import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Search, SlidersHorizontal, X } from 'lucide-react'
import { getProductsApi, searchProductsApi, getCategoriesApi } from '../../api/productApi'
import ProductCard from '../../components/ui/ProductCard'
import Spinner from '../../components/common/Spinner'

const ProductListPage = () => {
  const [searchParams, setSearchParams] = useSearchParams()

  const [products,    setProducts]    = useState([])
  const [categories,  setCategories]  = useState([])
  const [loading,     setLoading]     = useState(true)
  const [totalPages,  setTotalPages]  = useState(0)
  const [totalItems,  setTotalItems]  = useState(0)
  const [showFilters, setShowFilters] = useState(false)

  // Filter state
  const [search,     setSearch]     = useState(searchParams.get('q') || '')
  const [categoryId, setCategoryId] = useState(searchParams.get('category') || '')
  const [minPrice,   setMinPrice]   = useState(searchParams.get('minPrice') || '')
  const [maxPrice,   setMaxPrice]   = useState(searchParams.get('maxPrice') || '')
  const [page,       setPage]       = useState(0)

  // Load categories once
  useEffect(() => {
    getCategoriesApi()
      .then(res => setCategories(res.data.data || []))
      .catch(() => {})
  }, [])

  // Load products when filters change
  useEffect(() => {
    const fetchProducts = async () => {
      setLoading(true)
      try {
        let res
        if (search.trim()) {
          res = await searchProductsApi(search.trim(), page)
        } else {
          res = await getProductsApi({
            ...(categoryId && { categoryId }),
            ...(minPrice   && { minPrice }),
            ...(maxPrice   && { maxPrice }),
            page,
            size: 12,
            sortBy:  'createdAt',
            sortDir: 'desc',
          })
        }
        const data = res.data.data
        setProducts(data.content   || [])
        setTotalPages(data.totalPages  || 0)
        setTotalItems(data.totalElements || 0)
      } catch {
        setProducts([])
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [search, categoryId, minPrice, maxPrice, page])

  const handleSearchSubmit = (e) => {
    e.preventDefault()
    setPage(0)
    const params = {}
    if (search) params.q = search
    setSearchParams(params)
  }

  const clearFilters = () => {
    setSearch('')
    setCategoryId('')
    setMinPrice('')
    setMaxPrice('')
    setPage(0)
    setSearchParams({})
  }

  const hasFilters = search || categoryId || minPrice || maxPrice

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">

      {/* Page header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Shop</h1>
        {totalItems > 0 && !loading && (
          <p className="text-sm text-gray-500 mt-1">{totalItems} products found</p>
        )}
      </div>

      {/* Search + filter bar */}
      <div className="flex gap-3 mb-6">
        <form onSubmit={handleSearchSubmit} className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search products..."
            className="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-200 text-sm
                       focus:outline-none focus:ring-2 focus:ring-black/10 focus:border-black
                       transition-colors"
          />
        </form>

        <button
          onClick={() => setShowFilters(f => !f)}
          className="flex items-center gap-2 px-4 py-2.5 rounded-lg border border-gray-200
                     text-sm font-medium text-gray-700 hover:border-black transition-colors"
        >
          <SlidersHorizontal className="w-4 h-4" />
          Filters
          {hasFilters && (
            <span className="bg-black text-white text-xs rounded-full w-4 h-4
                             flex items-center justify-center">
              !
            </span>
          )}
        </button>

        {hasFilters && (
          <button
            onClick={clearFilters}
            className="flex items-center gap-1 px-3 py-2.5 rounded-lg text-sm
                       text-gray-500 hover:text-gray-900 transition-colors"
          >
            <X className="w-4 h-4" />
            Clear
          </button>
        )}
      </div>

      {/* Filter panel */}
      <AnimatePresence>
        {showFilters && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="overflow-hidden mb-6"
          >
            <div className="bg-gray-50 rounded-xl p-4 grid grid-cols-1 sm:grid-cols-3 gap-4">

              {/* Category filter */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">
                  Category
                </label>
                <select
                  value={categoryId}
                  onChange={e => { setCategoryId(e.target.value); setPage(0) }}
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm
                             focus:outline-none focus:border-black bg-white"
                >
                  <option value="">All categories</option>
                  {categories.map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>

              {/* Min price */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">
                  Min Price (৳)
                </label>
                <input
                  type="number"
                  value={minPrice}
                  onChange={e => { setMinPrice(e.target.value); setPage(0) }}
                  placeholder="0"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm
                             focus:outline-none focus:border-black"
                />
              </div>

              {/* Max price */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1.5">
                  Max Price (৳)
                </label>
                <input
                  type="number"
                  value={maxPrice}
                  onChange={e => { setMaxPrice(e.target.value); setPage(0) }}
                  placeholder="10000"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 text-sm
                             focus:outline-none focus:border-black"
                />
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Product grid */}
      {loading ? (
        <div className="py-20">
          <Spinner size="lg" />
        </div>
      ) : products.length === 0 ? (
        <div className="py-20 text-center">
          <p className="text-gray-500 text-sm">No products found.</p>
          {hasFilters && (
            <button
              onClick={clearFilters}
              className="mt-3 text-sm text-black font-medium hover:underline"
            >
              Clear filters
            </button>
          )}
        </div>
      ) : (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4"
        >
          {products.map((product, i) => (
            <motion.div
              key={product.id}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
            >
              <ProductCard product={product} />
            </motion.div>
          ))}
        </motion.div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-10">
          <button
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0}
            className="px-4 py-2 rounded-lg border border-gray-200 text-sm
                       disabled:opacity-40 hover:border-black transition-colors"
          >
            Previous
          </button>
          <span className="px-4 py-2 text-sm text-gray-600">
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="px-4 py-2 rounded-lg border border-gray-200 text-sm
                       disabled:opacity-40 hover:border-black transition-colors"
          >
            Next
          </button>
        </div>
      )}
    </div>
  )
}

export default ProductListPage