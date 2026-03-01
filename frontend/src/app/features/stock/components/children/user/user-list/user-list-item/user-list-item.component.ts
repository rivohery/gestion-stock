import {
  Component,
  Input,
  OnInit,
  effect,
  inject,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { UserDetailsResponse } from '../../../../../../auths/models/auths.model';
import { UserService } from '../../../../../../auths/services/user.service';
import { NgIf } from '@angular/common';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { ConfirmAlertDialogComponent } from '../../../../../../../shared/components/confirm-alert-dialog/confirm-alert-dialog.component';
import { UpdateUserStatusDialogComponent } from '../update-user-status-dialog/update-user-status-dialog.component';
import { SnackbarService } from '../../../../../../../shared/services/snackbar-service';

@Component({
  selector: 'app-user-list-item',
  imports: [
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    NgIf,
  ],
  templateUrl: './user-list-item.component.html',
  styleUrl: './user-list-item.component.css',
})
export class UserListItemComponent implements OnInit {
  @Input()
  userDetails?: UserDetailsResponse;

  profile?: string;

  userService = inject(UserService);
  dialog = inject(MatDialog);

  ngOnInit(): void {
    if (this.userDetails && this.userDetails.profileImageUrl) {
      this.userService
        .downloadProfile(this.userDetails.profileImageUrl)
        .subscribe({
          next: (image) => (this.profile = `data:image/jpg;base64,${image}`),
          error: (err) => console.log(err.message),
        });
    }
  }

  onEdit(userDetails: UserDetailsResponse): void {
    console.log('open update state dialog');
    this.userService.initUpdateUserInfosState();
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
    dialogConfig.width = '100%';
    dialogConfig.maxWidth = '450px';
    if (userDetails) {
      console.log(userDetails);
      dialogConfig.data = userDetails;
      const dialogRef = this.dialog.open(
        UpdateUserStatusDialogComponent,
        dialogConfig
      );
    }
  }

  onDelete(userId: number): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
    dialogConfig.width = '100%';
    dialogConfig.maxWidth = '400px';
    this.userService.initDeleteUserState();
    const dialogRef = this.dialog.open(
      ConfirmAlertDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe((confirm) => {
      if (confirm) {
        this.userService.deleteUserById(userId);
      }
    });
  }
}
