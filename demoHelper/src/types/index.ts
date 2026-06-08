// ===== API Request / Response =====

export interface GenerateRequest {
  intentText?: string
  imageBase64?: string
  apiKey?: string
  baseUrl?: string
  model?: string
  provider?: string
}

export interface GenerateResponse {
  id: string
  title: string
  description: string
  sections: PlanSection[]
  metadata: ProjectMetadata
}

// ===== Domain Models =====

export interface PlanSection {
  id: string
  order: number
  title: string
  description: string
  /** The complete prompt text ready to copy and paste into an AI coding tool */
  prompt: string
  estimatedHours: number
  difficulty: Difficulty
}

export interface ProjectMetadata {
  totalSections: number
  estimatedTotalHours: number
  difficulty: Difficulty
  generatedAt: string
}

// ===== Union Types =====

export type Difficulty = 'beginner' | 'intermediate' | 'advanced'
export type TabKey = 'cards' | 'markdown' | 'json'
export type AppState = 'idle' | 'loading' | 'success' | 'error'
