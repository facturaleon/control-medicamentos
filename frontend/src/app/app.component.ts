import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Medicamento {
  id: number;
  nombre: string;
}
interface DetalleFormulario {
  medicamentoId: number;
  nombre: string;
  ingreso: number;
  egreso: number;
  stockAnterior: number;
}
interface Reporte {
  id: number;
  unicodigo: string;
  mes: number;
  anio: number;
  estado: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  private api = 'http://localhost:8080/api';

  reportes: Reporte[] = [];
  medicamentos: Medicamento[] = [];
  unicodigo = '';
  periodo = '';
  medicamentoId: number | null = null;
  ingreso = 0;
  egreso = 0;
  detalles: DetalleFormulario[] = [];
  mensaje = '';
  tipoMensaje: 'ok' | 'error' = 'ok';
  cargando = false;

  minimo = '2025-07';
  maximo = this.obtenerMesActual();

  stockAnteriorFormulario = 0;
  consultandoStock = false;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.periodo = this.maximo;
    this.cargarDatos();
  }

  obtenerMesActual(): string {
    const fecha = new Date();
    return `${fecha.getFullYear()}-${String(fecha.getMonth() + 1).padStart(2, '0')}`;
  }

  cargarDatos(): void {
    this.http.get<Reporte[]>(`${this.api}/reportes`).subscribe({
      next: data => this.reportes = data,
      error: () => this.mostrarMensaje('No se pudieron cargar los reportes. Verifique que el backend esté ejecutándose.', 'error')
    });
    this.http.get<Medicamento[]>(`${this.api}/medicamentos`).subscribe({
      next: data => this.medicamentos = data,
      error: () => this.mostrarMensaje('No se pudo cargar el catálogo de medicamentos.', 'error')
    });
  }

  agregarMedicamento(): void {
    this.mensaje = '';

    if (this.medicamentoId === null) {
      this.mostrarMensaje('Seleccione un medicamento.', 'error');
      return;
    }

    if (!this.unicodigo.trim() || !this.periodo) {
      this.mostrarMensaje('Ingrese primero el UNICODIGO y el periodo.', 'error');
      return;
    }

    if (this.detalles.some(d => d.medicamentoId === Number(this.medicamentoId))) {
      this.mostrarMensaje('El medicamento ya fue agregado.', 'error');
      return;
    }

    if (!Number.isInteger(Number(this.ingreso)) ||
      !Number.isInteger(Number(this.egreso)) ||
      Number(this.ingreso) < 0 ||
      Number(this.egreso) < 0) {
      this.mostrarMensaje('Ingrese cantidades enteras iguales o mayores que cero.', 'error');
      return;
    }

    const medicamento = this.medicamentos.find(
      m => m.id === Number(this.medicamentoId)
    );

    if (!medicamento) {
      this.mostrarMensaje('Medicamento no encontrado.', 'error');
      return;
    }

    if (this.consultandoStock) {
      this.mostrarMensaje('Espere mientras se consulta el stock.', 'error');
      return;
    }

    const stockAnterior = this.stockAnteriorFormulario;
    const ingreso = Number(this.ingreso);
    const egreso = Number(this.egreso);

    if (egreso > stockAnterior + ingreso) {
      this.mostrarMensaje(
        `Stock insuficiente para ${medicamento.nombre}. Disponible: ${stockAnterior + ingreso}.`,
        'error'
      );
      return;
    }

    this.detalles.push({
      medicamentoId: medicamento.id,
      nombre: medicamento.nombre,
      ingreso,
      egreso,
      stockAnterior
    });

    this.medicamentoId = null;
    this.ingreso = 0;
    this.egreso = 0;
    this.stockAnteriorFormulario = 0;
  }

  quitarMedicamento(index: number): void {
    this.detalles.splice(index, 1);
  }

  guardar(): void {
    if (!this.unicodigo.trim() || !this.periodo || this.detalles.length === 0) {
      this.mostrarMensaje('Ingrese UNICODIGO, periodo y al menos un medicamento.', 'error');
      return;
    }

    const [anioTexto, mesTexto] = this.periodo.split('-');
    const anio = Number(anioTexto);
    const mes = Number(mesTexto);
    if (this.periodo < this.minimo || this.periodo > this.maximo) {
      this.mostrarMensaje('Seleccione un periodo válido entre julio de 2025 y el mes actual.', 'error');
      return;
    }

    const payload = {
      unicodigo: this.unicodigo.trim(),
      mes,
      anio,
      medicamentos: this.detalles.map(d => ({
        medicamentoId: d.medicamentoId,
        ingreso: d.ingreso,
        egreso: d.egreso
      }))
    };

    this.cargando = true;
    this.http.post<Reporte>(`${this.api}/reportes`, payload).subscribe({
      next: () => {
        this.cargando = false;
        this.mostrarMensaje('Reporte registrado correctamente.', 'ok');
        this.detalles = [];
        this.unicodigo = '';
        this.periodo = this.maximo;
        this.cargarDatos();
        this.stockAnteriorFormulario = 0;
      },
      error: error => {
        this.cargando = false;
        this.mostrarMensaje(error?.error?.mensaje || 'No se pudo guardar el reporte.', 'error');
      }
    });
  }

  eliminar(id: number): void {
    if (!confirm('¿Está seguro de eliminar este reporte? Esta acción no se puede deshacer.')) {
      return;
    }
    this.http.delete(`${this.api}/reportes/${id}`).subscribe({
      next: () => {
        this.mostrarMensaje('Reporte eliminado. Puede registrar nuevamente ese periodo.', 'ok');
        this.cargarDatos();
      },
      error: error => this.mostrarMensaje(error?.error?.mensaje || 'No se pudo eliminar el reporte.', 'error')
    });
  }

  nombreMes(mes: number): string {
    const nombres = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    return nombres[mes - 1] || String(mes);
  }

  mostrarMensaje(texto: string, tipo: 'ok' | 'error'): void {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }

  consultarStock(): void {
    this.stockAnteriorFormulario = 0;

    if (!this.unicodigo.trim() || !this.periodo || this.medicamentoId === null) {
      return;
    }

    const [anioTexto, mesTexto] = this.periodo.split('-');
    const anio = Number(anioTexto);
    const mes = Number(mesTexto);
    const medicamentoId = Number(this.medicamentoId);
    const unicodigo = this.unicodigo.trim();

    this.consultandoStock = true;

    this.http.get<{ stockAnterior: number }>(
      `${this.api}/reportes/stock`,
      {
        params: {
          unicodigo,
          medicamentoId: medicamentoId.toString(),
          mes: mes.toString(),
          anio: anio.toString()
        }
      }
    ).subscribe({
      next: respuesta => {
        // Evita mostrar el stock de una selección anterior
        // si el usuario cambió los campos durante la consulta.
        if (
          this.unicodigo.trim() === unicodigo &&
          this.periodo === `${anio}-${String(mes).padStart(2, '0')}` &&
          Number(this.medicamentoId) === medicamentoId
        ) {
          this.stockAnteriorFormulario = respuesta.stockAnterior ?? 0;
        }
        this.consultandoStock = false;
      },
      error: () => {
        this.consultandoStock = false;
        this.mostrarMensaje('No se pudo consultar el stock anterior.', 'error');
      }
    });
  }

  stockDisponible(): number {
    return this.stockAnteriorFormulario + Number(this.ingreso || 0);
  }

  stockFinal(ingreso: number, egreso: number, stockAnterior: number): number {
    return stockAnterior + Number(ingreso || 0) - Number(egreso || 0);
  }

  cambioContexto(): void {
    // El stock depende del establecimiento y del periodo.
    this.detalles = [];
    this.stockAnteriorFormulario = 0;
    this.consultarStock();
  }
  
  trackReporte(_: number, reporte: Reporte): number {
    return reporte.id;
  }
}
