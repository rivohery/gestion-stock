import {
  Component,
  Inject,
  OnInit,
  effect,
  inject,
  signal,
} from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserDetailsResponse } from '../../../../../../auths/models/auths.model';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { UpdateUserStatusRequest } from '../../../../../../auths/models/user.model';
import { NgIf } from '@angular/common';
import { UserService } from '../../../../../../auths/services/user.service';
import { SnackbarService } from '../../../../../../../shared/services/snackbar-service';

@Component({
  selector: 'app-update-user-status-dialog',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
    NgIf,
  ],
  templateUrl: './update-user-status-dialog.component.html',
  styleUrl: './update-user-status-dialog.component.css',
})
export class UpdateUserStatusDialogComponent implements OnInit {
  statusControl: FormControl<boolean> = new FormControl(false, {
    nonNullable: true,
  });
  userService = inject(UserService);

  loading = signal<boolean>(false);

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: UserDetailsResponse,
    private dialogRef: MatDialogRef<UpdateUserStatusDialogComponent>
  ) {}

  ngOnInit(): void {
    if (this.data) {
      console.log(this.data);
      this.statusControl.setValue(this.data.enabled);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    const request: UpdateUserStatusRequest = {
      id: this.data.userId,
      enabled: this.statusControl.value,
    };
    console.log(request);
    this.loading.set(true);
    this.userService.updateUserStatus(request);
    this.dialogRef.close();
  }
}
