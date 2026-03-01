import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { PurchaseOrderResponse } from '../../../puchase-order.model';
import { CurrencyPipe, DatePipe, NgClass, NgIf } from '@angular/common';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-invoice-view',
  imports: [MatIconModule, NgIf, NgClass, CurrencyPipe, DatePipe],
  templateUrl: './invoice-view.component.html',
  styleUrl: './invoice-view.component.css',
})
export class InvoiceViewComponent {
  @Input()
  purchaseOrdeDetails?: PurchaseOrderResponse;
  @Output()
  onPDfExported: EventEmitter<string> = new EventEmitter();

  @ViewChild('factureSection', { static: false }) factureElement!: ElementRef;

  genererPDF() {
    const data = this.factureElement.nativeElement;

    html2canvas(data, {
      scale: 2,
      useCORS: true, // Utile si vous avez des images externes
      logging: false,
      allowTaint: true,
      // Cette option aide parfois avec les problèmes de rendu SVG
      foreignObjectRendering: false,
    }).then((canvas) => {
      // Dimensions de l'image générée
      const imgWidth = 208; // Largeur A4 en mm
      const imgHeight = (canvas.height * imgWidth) / canvas.width;

      const contentDataURL = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4'); // Format A4, portrait
      const position = 0;

      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
      pdf.save('ma-facture.pdf');

      this.onPDfExported.emit('details');
    });
  }
}
