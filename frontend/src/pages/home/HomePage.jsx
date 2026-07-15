import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowRight, ShoppingBag, Truck, RotateCcw, Shield } from 'lucide-react'
import { getFeaturedProductsApi, getCategoriesApi } from '../../api/productApi'
import ProductCard from '../../components/ui/ProductCard'
import Spinner from '../../components/common/Spinner'

// ── Animation variants ────────────────────────────────────────────────────
const fadeUp = {
  hidden:  { opacity: 0, y: 32 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.5 } },
}

const stagger = {
  visible: { transition: { staggerChildren: 0.1 } },
}

// ── Features data ─────────────────────────────────────────────────────────
const FEATURES = [
  { icon: Truck,     label: 'Free Delivery',    sub: 'On orders over ৳1500' },
  { icon: RotateCcw, label: 'Easy Returns',     sub: '7-day return policy'  },
  { icon: Shield,    label: 'Secure Payment',   sub: 'bKash, Nagad & COD'   },
  { icon: ShoppingBag, label: 'Quality Assured', sub: 'Handpicked styles'   },
]

const HomePage = () => {
  const [featured,   setFeatured]   = useState([])
  const [categories, setCategories] = useState([])
  const [loading,    setLoading]    = useState(true)

  useEffect(() => {
    Promise.all([
      getFeaturedProductsApi(),
      getCategoriesApi(),
    ]).then(([featuredRes, catRes]) => {
      setFeatured(featuredRes.data.data?.content   || [])
      setCategories(catRes.data.data?.slice(0, 4)  || [])
    }).catch(() => {}).finally(() => setLoading(false))
  }, [])

  return (
    <div className="min-h-screen">

      {/* ── Hero ───────────────────────────────────────────────────────── */}
      <section className="bg-gray-950 text-white min-h-[90vh] flex items-center
                          relative overflow-hidden">

        {/* Background texture */}
        <div className="absolute inset-0 opacity-5"
          style={{
            backgroundImage: `radial-gradient(circle at 1px 1px, white 1px, transparent 0)`,
            backgroundSize: '32px 32px',
          }}
        />

        <div className="max-w-7xl mx-auto px-4 w-full relative z-10">
          <div className="max-w-2xl">

            <motion.p
              initial={{ opacity: 0, x: -16 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.4 }}
              className="text-gray-400 text-sm uppercase tracking-widest mb-4"
            >
              New Collection 2026
            </motion.p>

            <motion.h1
              initial={{ opacity: 0, y: 24 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.1 }}
              className="text-5xl sm:text-6xl lg:text-7xl font-bold leading-tight mb-6"
            >
              Dress for
              <br />
              <span className="text-gray-400">your moment</span>
            </motion.h1>

            <motion.p
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.25 }}
              className="text-gray-400 text-lg mb-10 leading-relaxed"
            >
              Premium men's clothing crafted for the modern Bangladeshi man.
              Quality you can feel, style you can own.
            </motion.p>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.35 }}
              className="flex flex-wrap gap-4"
            >
              <Link
                to="/products"
                className="inline-flex items-center gap-2 bg-white text-black
                           px-6 py-3.5 rounded-xl font-medium hover:bg-gray-100
                           transition-colors"
              >
                Shop Now
                <ArrowRight className="w-4 h-4" />
              </Link>

              <Link
                to="/products?featured=true"
                className="inline-flex items-center gap-2 border border-gray-700
                           text-white px-6 py-3.5 rounded-xl font-medium
                           hover:border-gray-500 transition-colors"
              >
                View Featured
              </Link>
            </motion.div>
          </div>
        </div>

        {/* Scroll indicator */}
        <motion.div
          animate={{ y: [0, 8, 0] }}
          transition={{ repeat: Infinity, duration: 2 }}
          className="absolute bottom-8 left-1/2 -translate-x-1/2"
        >
          <div className="w-5 h-8 rounded-full border border-gray-600 flex
                          items-start justify-center p-1">
            <div className="w-1 h-2 bg-gray-500 rounded-full" />
          </div>
        </motion.div>
      </section>

      {/* ── Features strip ─────────────────────────────────────────────── */}
      <section className="bg-white border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-4 py-8">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-6">
            {FEATURES.map(({ icon: Icon, label, sub }) => (
              <div key={label} className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gray-50 rounded-xl flex items-center
                                justify-center flex-shrink-0">
                  <Icon className="w-5 h-5 text-gray-700" />
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-900">{label}</p>
                  <p className="text-xs text-gray-400">{sub}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Categories ─────────────────────────────────────────────────── */}
      {categories.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 py-16">
          <motion.div
            variants={stagger}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
          >
            <motion.div variants={fadeUp} className="flex items-end justify-between mb-8">
              <div>
                <p className="text-xs text-gray-400 uppercase tracking-widest mb-1">
                  Browse by
                </p>
                <h2 className="text-2xl font-bold text-gray-900">Categories</h2>
              </div>
              <Link
                to="/products"
                className="text-sm text-gray-500 hover:text-gray-900
                           transition-colors flex items-center gap-1"
              >
                All products <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </motion.div>

            <motion.div
              variants={stagger}
              className="grid grid-cols-2 sm:grid-cols-4 gap-4"
            >
              {categories.map(cat => (
                <motion.div key={cat.id} variants={fadeUp}>
                  <Link
                    to={`/products?category=${cat.id}`}
                    className="group block aspect-square bg-gray-50 rounded-2xl
                               overflow-hidden relative hover:bg-gray-100
                               transition-colors"
                  >
                    {cat.imageUrl ? (
                      <img
                        src={cat.imageUrl}
                        alt={cat.name}
                        className="w-full h-full object-cover group-hover:scale-105
                                   transition-transform duration-500"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center">
                        <ShoppingBag className="w-10 h-10 text-gray-200" />
                      </div>
                    )}
                    <div className="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent" />
                    <div className="absolute bottom-4 left-4">
                      <p className="text-white font-semibold text-sm">{cat.name}</p>
                    </div>
                  </Link>
                </motion.div>
              ))}
            </motion.div>
          </motion.div>
        </section>
      )}

      {/* ── Featured products ───────────────────────────────────────────── */}
      <section className="max-w-7xl mx-auto px-4 pb-16">
        <motion.div
          variants={stagger}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
        >
          <motion.div variants={fadeUp} className="flex items-end justify-between mb-8">
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest mb-1">
                Handpicked
              </p>
              <h2 className="text-2xl font-bold text-gray-900">Featured</h2>
            </div>
            <Link
              to="/products"
              className="text-sm text-gray-500 hover:text-gray-900
                         transition-colors flex items-center gap-1"
            >
              View all <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </motion.div>

          {loading ? (
            <div className="py-16"><Spinner size="lg" /></div>
          ) : featured.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-gray-400 text-sm">No featured products yet.</p>
              <Link
                to="/products"
                className="mt-3 inline-block text-sm text-black font-medium hover:underline"
              >
                Browse all products
              </Link>
            </div>
          ) : (
            <motion.div
              variants={stagger}
              className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4"
            >
              {featured.slice(0, 8).map(product => (
                <motion.div key={product.id} variants={fadeUp}>
                  <ProductCard product={product} />
                </motion.div>
              ))}
            </motion.div>
          )}
        </motion.div>
      </section>

      {/* ── Banner CTA ──────────────────────────────────────────────────── */}
      <section className="bg-gray-950 text-white py-20 px-4">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
          className="max-w-2xl mx-auto text-center"
        >
          <h2 className="text-3xl font-bold mb-4">
            Your style, delivered
          </h2>
          <p className="text-gray-400 mb-8">
            Shop the latest men's fashion from Emras.
            Fast delivery across Bangladesh.
          </p>
          <Link
            to="/products"
            className="inline-flex items-center gap-2 bg-white text-black
                       px-6 py-3 rounded-xl font-medium hover:bg-gray-100
                       transition-colors"
          >
            Shop the Collection
            <ArrowRight className="w-4 h-4" />
          </Link>
        </motion.div>
      </section>

      {/* ── Footer ──────────────────────────────────────────────────────── */}
      <footer className="bg-gray-950 border-t border-gray-800 px-4 py-8">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row
                        items-center justify-between gap-4">
          <p className="text-xl font-bold tracking-widest text-white">EMRAS</p>
          <p className="text-xs text-gray-500">
            © 2026 Emras Clothing. All rights reserved.
          </p>
          <div className="flex gap-6 text-xs text-gray-500">
            <Link to="/products" className="hover:text-gray-300 transition-colors">
              Shop
            </Link>
            <Link to="/login" className="hover:text-gray-300 transition-colors">
              Account
            </Link>
          </div>
        </div>
      </footer>
    </div>
  )
}

export default HomePage