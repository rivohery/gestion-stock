import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { SearchComponent } from '../../../../../../shared/components/search/search.component';
import { UserListItemComponent } from './user-list-item/user-list-item.component';
import { PaginationComponent } from '../../../../../../shared/components/pagination/pagination.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { CreateUserDialogComponent } from './create-user-dialog/create-user-dialog.component';
import { PageResponse } from '../../../../../../shared/models/shared.model';
import { UserDetailsResponse } from '../../../../../auths/models/auths.model';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { NgIf } from '@angular/common';
import { UserService } from '../../../../../auths/services/user.service';
import { UpdateUserStatusDialogComponent } from './update-user-status-dialog/update-user-status-dialog.component';
import { CreateUserRequest } from '../../../../../auths/models/user.model';

@Component({
  selector: 'app-user-list',
  imports: [
    SearchComponent,
    UserListItemComponent,
    PaginationComponent,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    NgIf,
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css',
})
export class UserListComponent implements OnInit {
  dialog = inject(MatDialog);
  snackbar = inject(SnackbarService);
  userService = inject(UserService);
  userPages?: PageResponse<UserDetailsResponse>;

  loading = signal<boolean>(false);

  search: string = '';
  currentPage: number = 0;
  size: number = 3;

  constructor() {
    effect(() => {
      if (this.userService.createUserState().status === 'OK') {
        this.loading.set(false);
        const successMsg =
          this.userService.createUserState().value?.message || '';
        this.snackbar.openSnackBar(successMsg, 'success');
        this.userService.findAllUser();
      }
      if (this.userService.createUserState().status === 'ERROR') {
        this.loading.set(false);
        const errorMsg = this.userService.createUserState().error || '';
        this.snackbar.openSnackBar(errorMsg, 'error');
      }
    });
    effect(() => {
      if (this.userService.fetchAllUserState().status === 'OK') {
        this.loading.set(false);
        this.userPages = this.userService.fetchAllUserState().value;
      }
      if (this.userService.fetchAllUserState().status === 'ERROR') {
        this.loading.set(false);
        const errorMsg = this.userService.fetchAllUserState().error || '';
        this.snackbar.openSnackBar(errorMsg, 'error');
      }
    });

    effect(() => {
      if (this.userService.deleteUserByIdState().status === 'OK') {
        const successMsg =
          this.userService.deleteUserByIdState().value?.message || '';
        this.snackbar.openSnackBar(successMsg, 'success');
        this.userService.findAllUser();
      }
      if (this.userService.deleteUserByIdState().status === 'ERROR') {
        const errorMsg = this.userService.deleteUserByIdState().error || '';
        this.snackbar.openSnackBar(errorMsg, 'error');
      }
    });

    effect(() => {
      if (this.userService.updateUserStatusState().status === 'OK') {
        const successMsg =
          this.userService.updateUserStatusState().value?.message || '';
        this.userService.findAllUser();
        this.snackbar.openSnackBar(successMsg, 'success');
      }
      if (this.userService.updateUserStatusState().status === 'ERROR') {
        const errorMsg = this.userService.updateUserStatusState().error || '';
        this.snackbar.openSnackBar(errorMsg, 'error');
      }
    });
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.checkUserPages();
  }

  private checkUserPages(): void {
    this.userService.findAllUser(this.search, this.currentPage, this.size);
  }

  openDialogToAddUser(): void {
    this.userService.initCreateUserState();
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
    dialogConfig.autoFocus = true; // Focus automatique sur le premier champ
    dialogConfig.width = '100%';
    dialogConfig.maxWidth = '600px';

    const dialogRef = this.dialog.open(CreateUserDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log('Données reçues du formulaire :', result);
        this.loading.set(true);
        this.userService.createUser(result as CreateUserRequest);
      } else {
        console.log('Saisie annulée');
      }
    });
  }

  doSearch(value: string): void {
    this.initStates();
    this.search = value;
    this.currentPage = 0;
    console.log(this.search);
    this.checkUserPages();
  }

  goToPage(page: number): void {
    this.initStates();
    this.currentPage = page;
    this.checkUserPages();
  }

  private initStates(): void {
    this.userService.initUpdateUserInfosState();
    this.userService.initDeleteUserState();
  }
}
