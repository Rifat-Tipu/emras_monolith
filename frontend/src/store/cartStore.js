import { create } from 'zustand'
import { persist } from 'zustand/middleware'

const useCartStore = create(
  persist(
    (set, get) => ({
      items:     [],
      itemCount: 0,

      setCart: (items) => {
        set({
          items,
          itemCount: items.reduce((sum, item) => sum + item.quantity, 0),
        })
      },

      clearCart: () => set({ items: [], itemCount: 0 }),

      getItemCount: () => get().itemCount,
    }),
    {
      name: 'emras-cart',
    }
  )
)

export default useCartStore