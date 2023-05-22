package ar.edu.unahur.obj2.ventasAereas

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class VueloTest : DescribeSpec({
    describe("Aerolineas Argentinas") {
        val boeing747_8 = Avion(467, 19.4, 25000.0)
        val america = Continente()
        val asia = Continente()
        val tokio = Ciudad(asia)
        val rioDeJaneiro = Ciudad(america)
        val buenosAires = Ciudad(america)
        val cuzco = Ciudad(america)
        val asuncion = Ciudad(america)
        val cancun = Ciudad(america)
        val vueloATokio = DePasajeros(
            LocalDate.now(), boeing747_8,
            rioDeJaneiro, tokio, 50000.0, Estricta, 40.0
        )
        val vueloACancun = DeCarga(
            LocalDate.of(2021, 5, 14),
            boeing747_8, buenosAires, cancun, 26000.0, Estricta, 500.0
        )
        val vueloARioDeJaneiro = DePasajeros(
            LocalDate.of(2021, 5, 14),
            boeing747_8, buenosAires, rioDeJaneiro, 26000.0, Estricta, 25.0
        )
        val vueloACuzco = DeCarga(
            LocalDate.of(2021, 5, 14),
            boeing747_8, buenosAires, cuzco, 26000.0, Estricta, 500.0
        )
        val vueloAAsuncion = Charter(
            LocalDate.of(2021, 5, 14),
            boeing747_8, buenosAires, asuncion, 26000.0, Estricta, 0
        )
        Vuelo.criterioActual = Segura

        describe("Un avión") {
            describe("con vuelo a Rio de Janeiro") {
                describe("con politica de venta Estricta") {
                    describe("de pasajeros") {
                        it("tiene disponible todos sus asientos") {
                            vueloARioDeJaneiro.asientosDisponibles().shouldBe(467)
                        }
                        it("no puede vender en pandemia") {
                            Vuelo.criterioActual = Pandemia
                            vueloARioDeJaneiro.puedeVenderPasajes().shouldBeFalse()
                        }
                    }
                    it("tiene como precio de venta el precio Estandar") {
                        vueloAAsuncion.precio().shouldBe(26000.0)
                    }
                    it("Peso maximo con 2 pasajeros") {
                        vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 20), 38066777)
                        vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 20), 38061111)
                        vueloARioDeJaneiro.pesoMaximo().shouldBe(25220)
                    }
                }
                describe("con politica de Venta Anticipada") {
                    vueloARioDeJaneiro.politica = VentaAnticipada
                    describe("con menos de 40 pasajes vendidos") {
                        it("tiene como precio de venta el 30% del precio Estandar") {
                            vueloARioDeJaneiro.precio().shouldBe(7800.0)
                        }

                    }
                    describe("que tiene entre 40 y 79 pasajes vendidos") {
                        repeat(50) { vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 14), 36722394) }
                        it("tiene como precio de venta el 60% del precio Estandar") {
                            vueloARioDeJaneiro.precio().shouldBe(15600.0)
                        }
                    }
                    describe("con mas de 79 pasajes vendidos") {
                        repeat(80) { vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 14), 36722394) }
                        it("tiene como precio de venta el precio Estandar") {
                            vueloARioDeJaneiro.precio().shouldBe(26000.0)
                        }
                        it("se sabe en total cuantos asientos libres tiene entre 2 fechas") {
                            IATA.totalAsientosLibresEntreDosFechasDe(
                                rioDeJaneiro,
                                LocalDate.of(2020, 5, 14),
                                LocalDate.of(2021, 12, 14)
                            ).shouldBe(387)
                        }
                    }
                }

                describe("con politica de venta de Remate") {
                    vueloARioDeJaneiro.politica = Remate
                    describe("de Pasajeros con mas de 30 asientos libres") {
                        it("tiene como precio de venta el 25% del precio Estandar") {
                            vueloARioDeJaneiro.precio().shouldBe(6500.0)
                        }
                        it("NO es relajado") {
                            vueloARioDeJaneiro.esRelajado().shouldBeFalse()
                        }
                    }
                }
            }

            describe("con vuelo a Cuzco") {
                describe("con política de Venta Estricta") {
                    describe("de carga") {
                        it("tiene disponible 10 asientos") {
                            vueloACuzco.asientosDisponibles().shouldBe(10)
                        }
                    }
                }
                describe("con politica de Venta de Remate") {
                    vueloACuzco.politica = Remate
                    describe("de Carga con menos de 30 asientos libres") {
                        it("tiene como precio de venta el 50% del precio Estandar") {
                            vueloACuzco.precio().shouldBe(13000.0)
                            //tiene 10 asientos libres para pasajeros
                        }
                        it("es relajado") {
                            vueloACuzco.esRelajado().shouldBeTrue()
                        }
                        it("vende correctamente un pasaje") {
                            vueloACuzco.venderPasaje(LocalDate.of(2021, 5, 20), 38666666)
                            vueloACuzco.pasajesVendidos.size.shouldBe(1)
                        }
                        it("arroja un mensaje de error por no poder vender en pandemia") {
                            Vuelo.criterioActual = Pandemia
                            shouldThrowAny { vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 20), 38666666) }
                        }
                    }
                }
            }

            describe("con vuelo a Asuncion") {
                describe("con política de Venta Estricta") {
                    describe("charter") {
                        it("tiene disponible todos sus asientos - 25") {
                            vueloAAsuncion.asientosDisponibles().shouldBe(442)
                        }
                        it("peso maximo sin pasajeros.") {
                            vueloAAsuncion.pesoMaximo().shouldBe(30000)
                        }
                        it("peso maximo con 2 pasajeros") {
                            vueloAAsuncion.venderPasaje(LocalDate.of(2021, 5, 20), 38066777)
                            vueloAAsuncion.venderPasaje(LocalDate.of(2021, 5, 20), 31865564)
                            vueloAAsuncion.pesoMaximo().shouldBe(30170)
                        }
                    }
                }
            }
        }
        describe("Una persona") {
            describe("con 1 pasaje comprado del vuelo a Asuncion") {
                vueloAAsuncion.venderPasaje(LocalDate.of(2021, 5, 12), 36722394)
                it("la IATA sabe en qué fecha viaja") {
                    IATA.fechasQueViajaUnaPersonaA(asuncion, 36722394).shouldContain(LocalDate.of(2021, 5, 14))
                }
            }
            describe("con 3 pasajes de vuelos distintos") {
                vueloAAsuncion.venderPasaje(LocalDate.of(2021, 3, 2), 37558962)
                vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 3, 2), 37558962)
                vueloACancun.venderPasaje(LocalDate.of(2021, 3, 2), 37558962)
                it("es compañera de otra persona que tiene pasajes para los mismos vuelos") {
                    vueloAAsuncion.venderPasaje(LocalDate.of(2021, 3, 2), 39444666)
                    vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 3, 2), 39444666)
                    vueloACancun.venderPasaje(LocalDate.of(2021, 3, 2), 39444666)
                    IATA.dosPersonasSonCompanieras(37558962, 39444666).shouldBeTrue()
                }
            }
        }

        describe("Importe total por venta de pasajes") {
            describe("vuelo a rio con venta anticipada y 2 pasajes vendidos.") {
                vueloARioDeJaneiro.politica = VentaAnticipada
                vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 20), 38066777)
                vueloARioDeJaneiro.venderPasaje(LocalDate.of(2021, 5, 20), 39066111)
                it("recauda 15600 por la venta de sus primeros 2 pasajes") {
                    vueloARioDeJaneiro.importeTotalPorVentaDePasajes().shouldBe(15600)
                }
            }
        }
        describe("Un avión con vuelo de carga a cancun") {
            it("tiene un peso maximo sin pasajeros de 26200") {
                vueloACancun.pesoMaximo().shouldBe(26200)
            }
            it("tiene un peso maximo con 2 pasajeros de 26370") {
                vueloACancun.venderPasaje(LocalDate.of(2021, 5, 20), 38066777)
                vueloACancun.venderPasaje(LocalDate.of(2021, 6, 21), 38066111)
                vueloACancun.pesoMaximo().shouldBe(26370)
            }
        }
        describe("Un avión con vuelo de Pasajeros Intercontinental que sale hoy") {
            it("la IATA sabe qué vuelos intercontinentales salen determinada fecha") {
                IATA.vuelosIntercontinentalesProgramadosElDia(LocalDate.now()).shouldContain(vueloATokio)
            }
        }
        describe("vuelo desde asuncion con criterio propio seguro") {
            Vuelo.criterioActual = Pandemia
            asuncion.criterioCiudad = Segura
            val vueloAAsuncion2 = DePasajeros(
                LocalDate.now(), boeing747_8,
                asuncion, tokio, 50000.0, Estricta, 40.0
            )
            it("asuncion permite vender pasajes porque esta segura") {
                vueloAAsuncion2.puedeVenderPasajes().shouldBeTrue()
            }

        }
    }
})
