// src/app/components/document-upload/document-upload.component.ts
import { Component } from '@angular/core';
import { DocumentService } from '../service/document.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.component.html',
  styleUrl: './document-upload.component.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class DocumentUploadComponent {
  selectedFile: File | null = null;
  uploadProgress: number = 0;
  errorMessage: string = '';

  constructor(private documentService: DocumentService) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.errorMessage = ''; 
    }
  }

  uploadFile(): void {
    if (!this.selectedFile) return;

    const metadata = {
      uploadedBy: 'user',
      department: 'test'
    };


    this.documentService.uploadDocument(this.selectedFile, metadata)
      .subscribe({
        next: (response) => {
          console.log('Upload successful', response);
          this.uploadProgress = 100;
        },
        error: (error) => {
          console.error('Upload failed', error);
          this.errorMessage = error.message;
          this.uploadProgress = 0;
        }
      });
  }
}