import { create } from 'zustand'
import { persist } from 'zustand/middleware'

const useAuthStore = create(
  persist(
    (set, get) => ({
      user:        null,
      accessToken: null,
      isLoggedIn:  false,

      setAuth: (user, accessToken) => {
        localStorage.setItem('accessToken', accessToken)
        set({ user, accessToken, isLoggedIn: true })
      },

      clearAuth: () => {
        localStorage.removeItem('accessToken')
        set({ user: null, accessToken: null, isLoggedIn: false })
      },

      getUser:  () => get().user,
      getToken: () => get().accessToken,
    }),
    {
      name: 'emras-auth',
      partialize: (state) => ({ user: state.user, isLoggedIn: state.isLoggedIn }),
    }
  )
)

export default useAuthStore