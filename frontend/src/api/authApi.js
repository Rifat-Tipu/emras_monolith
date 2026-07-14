import axiosInstance from './axiosInstance'
import { AUTH_ENDPOINTS } from '../constants/api'

export const registerApi = (data) =>
  axiosInstance.post(AUTH_ENDPOINTS.REGISTER, data)

export const loginApi = (data) =>
  axiosInstance.post(AUTH_ENDPOINTS.LOGIN, data)

export const refreshApi = () =>
  axiosInstance.post(AUTH_ENDPOINTS.REFRESH)

export const logoutApi = () =>
  axiosInstance.post(AUTH_ENDPOINTS.LOGOUT)