package ar.edu.unahur.obj2.ventasAereas

import java.time.LocalDate

object IATA {
    var vuelos = mutableListOf<Vuelo>()
    var pesoEstandarDePasajeros:Double=85.0

    fun fechasQueViajaUnaPersonaA(ciudad: Ciudad, dni: Int) = vuelos.map { it.fechaQueViajaUnaPersonaA(ciudad, dni) } // Requerimiento 8 Realizado.
    fun vuelosEntreDosFechas(fecha1: LocalDate, fecha2: LocalDate) = vuelos.filter { (it.fecha.isAfter(fecha1)) && it.fecha.isBefore(fecha2) }
    fun vuelosEntreDosFechasConDestinoA(destino: Ciudad, fecha1: LocalDate, fecha2: LocalDate) = this.vuelosEntreDosFechas(fecha1, fecha2).filter { it.destino == destino }
    fun totalAsientosLibresEntreDosFechasDe(destino: Ciudad, fecha1: LocalDate, fecha2: LocalDate) = this.vuelosEntreDosFechasConDestinoA(destino, fecha1, fecha2).sumBy { it.asientosLibres() } //Requerimiento 9 Realizado.
    fun encontrarVuelosDePasajero(dni: Int) = vuelos.map { it.pasajeDePersonaConDni(dni) }.map { it?.vuelo }
    fun dosPersonasSonCompanieras(dniPasajero1: Int, dniPasajero2: Int): Boolean {
        val vuelosDePasajero1 = this.encontrarVuelosDePasajero(dniPasajero1)
        val vuelosDePasajero2 = this.encontrarVuelosDePasajero(dniPasajero2)
        return vuelosDePasajero2.intersect(vuelosDePasajero1).size >= 3
    } // Requerimiento 10 realizado.
    fun vuelosDelDia(fecha: LocalDate) = vuelos.filter { it.fecha == fecha }
    fun vuelosIntercontinentalesProgramadosElDia(fecha: LocalDate) = this.vuelosDelDia(fecha).filter { it.origen.continente != it.destino.continente } // Bonus 2 Realizado.
}