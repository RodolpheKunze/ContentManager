// src/app/components/document-list/document-list.component.ts
import { Component, OnInit } from '@angular/core';
import { DocumentService } from '../../service/document.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface PageResponse {
  content: any[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.component.html',
  styleUrl: './document-list.component.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})

export class DocumentListComponent implements OnInit {
  documents: PageResponse | null = null;
  searchTerm: string = '';
  currentPage: number = 0;
  pageSize: number = 10;

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.documentService.getDocuments(this.searchTerm, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          console.log("search term:", this.searchTerm)
          this.documents = response;
        },
        error: (error) => {
          console.error('Failed to load documents', error);
        }
      });
  }

  search(): void {
    this.currentPage = 0; // Reset to first page when searching
    this.loadDocuments();
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadDocuments();
  }

  downloadDocument(storageKey: string): void {
    this.documentService.downloadDocument(storageKey)
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'document'; // You might want to use the actual filename
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
          a.remove();
        },
        error: (error) => {
          console.error('Download failed', error);
        }
      });
  }

  formatSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

}