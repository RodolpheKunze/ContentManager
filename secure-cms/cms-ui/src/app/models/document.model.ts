export interface DocumentMetadata {
    id?: string;
    filename: string;
    contentType: string;
    storageKey?: string;
    size: number;
    uploadDate?: Date;
    lastModifiedDate?: Date;
    customMetadata?: { [key: string]: string };
  }
  
  export interface DocumentUploadRequest {
    filename: string;
    content: File;
    contentType: string;
    metadata?: { [key: string]: string };
  }

  export interface DocumentUploadResponse {
    storageKey: string;
    filename: string;
}