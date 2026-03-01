export interface UpdateUserInfoRequest {
  userId: number;
  email?: string;
  firstName?: string;
  lastName?: string;
  phoneNu?: string;
}

export type RoleEnum = 'ADMIN' | 'STOCK_MANAGER' | 'SALES_MANAGER' | 'VIEWER';

export interface CreateUserRequest {
  firstName: string;
  lastName: string;
  phoneNu: string;
  email: string;
  roles: RoleEnum[];
}

export interface UpdateUserStatusRequest {
  id: number;
  enabled: boolean;
}
