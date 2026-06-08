import { ref } from 'vue'
import { validateImageFile } from '../utils/validators'

export function useImageUpload() {
  const imageBase64 = ref<string | null>(null)
  const imageName = ref<string | null>(null)
  const isDragging = ref(false)
  const error = ref<string | null>(null)

  function handleFileSelect(file: File) {
    error.value = null

    const validation = validateImageFile(file)
    if (!validation.valid) {
      error.value = validation.error
      return
    }

    const reader = new FileReader()
    reader.onload = () => {
      imageBase64.value = reader.result as string
      imageName.value = file.name
    }
    reader.onerror = () => {
      error.value = '图片读取失败，请重试'
    }
    reader.readAsDataURL(file)
  }

  function handleDrop(event: DragEvent) {
    isDragging.value = false
    const files = event.dataTransfer?.files
    if (files && files.length > 0) {
      handleFileSelect(files[0])
    }
  }

  function handleDragOver(event: DragEvent) {
    event.preventDefault()
    isDragging.value = true
  }

  function handleDragLeave(event: DragEvent) {
    // Only set false if leaving the drop zone itself
    if ((event.currentTarget as HTMLElement)?.contains(event.relatedTarget as Node)) {
      return
    }
    isDragging.value = false
  }

  function openFilePicker() {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/png,image/jpeg,image/gif,image/webp'
    input.onchange = () => {
      const file = input.files?.[0]
      if (file) handleFileSelect(file)
    }
    input.click()
  }

  function removeImage() {
    imageBase64.value = null
    imageName.value = null
    error.value = null
  }

  return {
    imageBase64,
    imageName,
    isDragging,
    error,
    handleFileSelect,
    handleDrop,
    handleDragOver,
    handleDragLeave,
    openFilePicker,
    removeImage,
  }
}
