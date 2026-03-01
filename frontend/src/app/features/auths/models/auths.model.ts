export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface OTPTokenRequest {
  token: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface UpdatePasswordRequest {
  email: string;
  oldPassword: string;
  newPassword: string;
}

export interface UserDetailsResponse {
  userId: number;
  fullName: string;
  profileImageUrl?: string;
  phoneNu: string;
  email: string;
  enabled: boolean;
  role: string;
}
