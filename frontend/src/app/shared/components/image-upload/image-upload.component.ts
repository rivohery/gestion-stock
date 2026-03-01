import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-image-upload',
  imports: [NgClass],
  templateUrl: './image-upload.component.html',
  styleUrl: './image-upload.component.css',
})
export class ImageUploadComponent {
  @Input() isUploading?: boolean;
  @Input() image?: string;
  @Output() imageUploadEvent: EventEmitter<File> = new EventEmitter();

  imagePreview: string | ArrayBuffer | null = null;
  isDragging = false;
  selectedFile: File | null = null;

  /*  // Déclencher le clic sur l'input invisible
  triggerFileInput(fileInput: HTMLInputElement) {
    fileInput.click();
  } */

  // Gérer la sélection via l'explorateur de fichiers
  onFileSelected(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let file: File | null = element.files ? element.files[0] : null;
    if (file) {
      this.handleFile(file);
    }
  }

  // Gérer le Glisser-Déposer (Drag & Drop)
  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files.length) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  // Lecture du fichier pour la prévisualisation
  private handleFile(file: File) {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(file);
      this.selectedFile = file;
    } else {
      alert('Veuillez sélectionner une image valide.');
    }
  }

  uploadImage(): void {
    if (this.selectedFile) {
      this.imageUploadEvent.emit(this.selectedFile);
    }
  }
}
