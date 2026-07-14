export const API_BASE = '/api/v1'

export const AUTH_ENDPOINTS = {
  REGISTER: `${API_BASE}/auth/register`,
  LOGIN:    `${API_BASE}/auth/login`,
  REFRESH:  `${API_BASE}/auth/refresh`,
  LOGOUT:   `${API_BASE}/auth/logout`,
}

export const PRODUCT_ENDPOINTS = {
  LIST:     `${API_BASE}/products`,
  SEARCH:   `${API_BASE}/products/search`,
  FEATURED: `${API_BASE}/products/featured`,
  BY_SLUG:  (slug) => `${API_BASE}/products/slug/${slug}`,
  BY_ID:    (id)   => `${API_BASE}/products/${id}`,
}

export const CATEGORY_ENDPOINTS = {
  LIST:       `${API_BASE}/categories`,
  BY_GENDER:  (gender) => `${API_BASE}/categories/gender/${gender}`,
  BY_SLUG:    (slug)   => `${API_BASE}/categories/slug/${slug}`,
}