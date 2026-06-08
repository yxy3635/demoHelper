const ALLOWED_IMAGE_TYPES = ['image/png', 'image/jpeg', 'image/gif', 'image/webp']
const MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
const MAX_INTENT_LENGTH = 5000

export interface ValidationResult {
  valid: boolean
  error: string | null
}

export function validateIntentText(text: string): ValidationResult {
  if (!text || text.trim().length === 0) {
    return { valid: false, error: '请输入项目意图描述' }
  }
  if (text.length > MAX_INTENT_LENGTH) {
    return { valid: false, error: `描述不能超过 ${MAX_INTENT_LENGTH} 个字符` }
  }
  return { valid: true, error: null }
}

export function validateImageFile(file: File): ValidationResult {
  if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
    return { valid: false, error: '仅支持 PNG、JPEG、GIF、WebP 格式的图片' }
  }
  if (file.size > MAX_IMAGE_SIZE) {
    const sizeMB = (file.size / 1024 / 1024).toFixed(1)
    return { valid: false, error: `图片大小不能超过 10MB（当前 ${sizeMB}MB）` }
  }
  return { valid: true, error: null }
}

export function validateGenerateInput(
  intentText: string,
  imageBase64: string | null,
): ValidationResult {
  const hasText = intentText.trim().length > 0
  const hasImage = imageBase64 !== null

  if (!hasText && !hasImage) {
    return { valid: false, error: '请至少输入文字描述或上传一张图片' }
  }
  if (hasText && intentText.length > MAX_INTENT_LENGTH) {
    return { valid: false, error: `文字描述不能超过 ${MAX_INTENT_LENGTH} 个字符` }
  }
  return { valid: true, error: null }
}
