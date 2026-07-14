import axiosInstance from './axiosInstance'
import { PRODUCT_ENDPOINTS, CATEGORY_ENDPOINTS } from '../constants/api'

export const getProductsApi = (params) =>
  axiosInstance.get(PRODUCT_ENDPOINTS.LIST, { params })

export const searchProductsApi = (q, page = 0) =>
  axiosInstance.get(PRODUCT_ENDPOINTS.SEARCH, { params: { q, page } })

export const getFeaturedProductsApi = () =>
  axiosInstance.get(PRODUCT_ENDPOINTS.FEATURED)

export const getProductBySlugApi = (slug) =>
  axiosInstance.get(PRODUCT_ENDPOINTS.BY_SLUG(slug))

export const getCategoriesApi = () =>
  axiosInstance.get(CATEGORY_ENDPOINTS.LIST)

export const getCategoriesByGenderApi = (gender) =>
  axiosInstance.get(CATEGORY_ENDPOINTS.BY_GENDER(gender))