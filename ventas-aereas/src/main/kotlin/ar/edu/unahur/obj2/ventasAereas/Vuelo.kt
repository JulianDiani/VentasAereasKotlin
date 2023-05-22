package ar.edu.unahur.obj2.ventasAereas

import java.time.LocalDate

abstract class Vuelo(val fecha: LocalDate, val avion: Avion, val origen: Ciudad, val destino: Ciudad,
                     val precioEstandar: Double,var politica: Politica){
    init {
        IATA.vuelos.add(this)
        if(this.origen.criterioCiudad!=null){ // BONUS 1!!!
            criterioActual= origen.criterioCiudad!!
        }
    }
    companion object {
        var criterioActual: Criterio = Pandemia
    }

    var pasajesVendidos = mutableListOf<Pasaje>()
    abstract fun asientosDisponibles(): Int
    abstract fun asientosOcupados(): Int
    fun asientosLibres() = this.asientosDisponibles() - this.asientosOcupados() //Requerimiento 1 Realizado.
    fun asientosVendidos() = pasajesVendidos.size
    fun precio() = politica.precio(this) // Requerimiento 4 Realizado.
    fun esRelajado() = avion.alturaCabina > 4 && this.asientosDisponibles() < 100 // Requerimiento 2 Realizado.
    fun puedeVenderPasajes()= criterioActual.permiteVenderPasajes(this)//Requerimiento 3 realizado.
    fun venderPasaje(fechaDeVenta: LocalDate, dni: Int){
        if(!this.puedeVenderPasajes()) {
            error("No se puede vender pasaje debido al criterio actual.")
        }
        pasajesVendidos.add( Pasaje( this, fechaDeVenta, dni, this.precio() ) ) // Requerimiento 5 realizado.
    }
    fun importeTotalPorVentaDePasajes()=pasajesVendidos.sumByDouble { p->p.precio }//requerimiento 6
    abstract fun pesoDeLaCarga():Double
    fun pesoDeLosPasajeros()=this.asientosOcupados()*IATA.pesoEstandarDePasajeros
    fun pasajeDePersonaConDestinoA(ciudad: Ciudad, dni: Int) = pasajesVendidos.find { it.dni == dni && it.vuelo.destino == ciudad }
    fun fechaQueViajaUnaPersonaA(ciudad: Ciudad, dni: Int) = this.pasajeDePersonaConDestinoA(ciudad, dni)?.vuelo?.fecha
    fun pesoMaximo()=avion.peso+this.pesoDeLosPasajeros()+this.pesoDeLaCarga()//requerimiento 7
    fun pasajeDePersonaConDni(dni: Int) = pasajesVendidos.find { it.dni == dni }
}

class DePasajeros(fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad,
                  precioEstandar: Double, politica: Politica,var pesoMaximoDeCargaParaPasajeros:Double):
    Vuelo(fecha,avion,origen,destino,precioEstandar,politica){
    override fun asientosDisponibles() = avion.cantidadAsientos
    override fun asientosOcupados() = pasajesVendidos.size
    override fun pesoDeLaCarga()= this.asientosOcupados()*pesoMaximoDeCargaParaPasajeros
}

class DeCarga(fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad,
              precioEstandar: Double, politica: Politica, val pesoDeLaCargaInicial: Double):
    Vuelo(fecha,avion,origen,destino,precioEstandar,politica){
    override fun asientosDisponibles() = 10
    override fun asientosOcupados() = pasajesVendidos.size
    override fun pesoDeLaCarga()= pesoDeLaCargaInicial+700
}

class Charter(fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad,
              precioEstandar: Double, politica: Politica, val cantPasajerosVip: Int):
    Vuelo(fecha,avion,origen,destino,precioEstandar,politica){

    override fun pesoDeLaCarga()=5000.0
    override fun asientosDisponibles() = avion.cantidadAsientos - 25 - cantPasajerosVip
    override fun asientosOcupados() = cantPasajerosVip + pasajesVendidos.size

}

class Pasaje(val vuelo: Vuelo, val fechaVenta: LocalDate, val dni: Int,val precio:Double)

abstract class Politica {
    abstract fun precio(vuelo: Vuelo): Double
}

object Estricta: Politica() {
    override fun precio(vuelo: Vuelo) = vuelo.precioEstandar
}

object VentaAnticipada: Politica() {
    override fun precio(vuelo: Vuelo) = when {
        vuelo.asientosVendidos() < 40 -> vuelo.precioEstandar * 0.3
        vuelo.asientosVendidos() in 40..79 -> vuelo.precioEstandar * 0.6
        else -> vuelo.precioEstandar
    }
}

object Remate: Politica() {
    override fun precio(vuelo: Vuelo) = if(vuelo.asientosLibres() > 30) vuelo.precioEstandar * 0.25 else vuelo.precioEstandar * 0.5
}

abstract class Criterio{
    abstract fun permiteVenderPasajes(vuelo: Vuelo): Boolean
}
object Segura:Criterio() {
    override fun permiteVenderPasajes(vuelo: Vuelo)= vuelo.asientosLibres()>=3
}
object LaxaFija:Criterio() {
    override fun permiteVenderPasajes(vuelo: Vuelo)= vuelo.asientosVendidos()<=vuelo.asientosDisponibles()+10
}
object LaxaPorcentual:Criterio() {
    override fun permiteVenderPasajes(vuelo: Vuelo)= vuelo.asientosVendidos()<=vuelo.asientosDisponibles()*1.1
}
object Pandemia:Criterio() {
    override fun permiteVenderPasajes(vuelo: Vuelo)= false
}