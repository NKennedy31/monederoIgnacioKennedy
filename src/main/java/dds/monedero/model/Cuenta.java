package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  /*public Cuenta() { //CODE SMELL: Logica Duplicada 
    saldo = 0;
  }*/

  /*public Cuenta() { // REDUNDANTE(montoInicial de donde sale)
    saldo = montoInicial;
  }*/

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    if (esMontoPositivo(cuanto) && noExcedioLaMaximaCantidadDeDepositos()) {
    	//new Movimiento(LocalDate.now(), cuanto, true).agregateA(this); CODE SMELL: FEATURE ENVY
    	this.setSaldo(calcularValor(true, cuanto));
    	this.agregarMovimiento(LocalDate.now(), cuanto, true);
  }
  }
  public void sacar(double cuanto) { 
    if (esMontoPositivo(cuanto) && noExcedeSaldoMenor(cuanto) && noSuperaMaximoExtraccionDiario(cuanto)) {
    	this.setSaldo(calcularValor(false, cuanto));
    	this.agregarMovimiento(LocalDate.now(), cuanto, false);
    }
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double calcularValor(boolean valor, double monto) {
	    if (valor) {
	      return this.getSaldo() + monto;
	    } else {
	      return this.getSaldo() - monto;
	    }
  }
	    
  public double getMontoExtraidoA(LocalDate fecha) { 
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }
  
  //CODE SMELL: LOGICA DUPLICADA 
  public boolean esMontoPositivo(double monto) {
	  if(monto <= 0) {
		  throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
	  }
	  else {
		  return true;
	  }
  }
  //CODE SMELL: LONG METHOD
  
  public boolean noExcedeSaldoMenor(double monto) {
	    if (getSaldo() - monto < 0) {
	        throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
	      }
	    else {
	    	return true;
	    }
  }
  
  public boolean noSuperaMaximoExtraccionDiario(double monto) {
	  double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
	  double limite = 1000 - montoExtraidoHoy;
	    if (monto > limite) {
	      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
	          + " diarios, límite: " + limite);
	    }
	    else {
	    	return true;
	    }
  }
  
  public boolean noExcedioLaMaximaCantidadDeDepositos() {
	   if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
	        throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
	    }
	   else {
		   return true;
	   }
  }
}
