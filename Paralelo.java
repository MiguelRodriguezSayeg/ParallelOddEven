package bubblesortparallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Paralelo{

private static final int MAXELEMS = 10000000;
private static final int DEFELEMS = 200000;
private static final int MINELEMS = 100000;

static int []elem;
static int elems = DEFELEMS;

// Aquí puedes agregar variables globales y/o predefinición de funciones.
public static class Worker extends Thread{
    int initialVal,finalVal;
    public Worker(int init, int end){
        this.initialVal=init;
        this.finalVal=end;
    }
    public void run(){
    boolean isSorted=false;
    
    while(!isSorted){
        isSorted=true;
        int temp=0;
        
        for(int i=initialVal+1; i<finalVal-1;i=i+2){
            if(elem[i]>elem[i+1]){
                temp=elem[i];
                elem[i]=elem[i+1];
                elem[i+1]=temp;
                isSorted=false;
            }
        }
        for(int i=initialVal; i<finalVal-1;i=i+2){
            if(elem[i]>elem[i+1]){
                temp=elem[i];
                elem[i]=elem[i+1];
                elem[i+1]=temp;
                isSorted=false;
            }
        }
    }
    
    }
}

public static void main(String []args){
	int i;
	int errores=0;
	long  before, after;
	elem = new int[MAXELEMS];
	if (args.length == 0)
		elems=DEFELEMS;
	else if(args.length == 1)
	{
		elems= Integer.parseInt(args[0]);
		if(elems<MINELEMS || elems>MAXELEMS)
		{
			System.out.printf("Número inválido, el mínimo es %d y el máximo es %d\n",MINELEMS,MAXELEMS);
			System.exit(1);
		}
	}
	else
	{
		System.out.printf("Número inválido de argumentos\n");
		System.exit(1);
	}
	
	System.out.printf("Ordenando " + elems + " elementos\n");

// Inicializa el arreglo con números random
	for(i=0;i<elems;i++)
		elem[i] = (int)(Math.random()* 100 + 1);


	before = System.currentTimeMillis();
	sort(elem,elems);
	after = System.currentTimeMillis();
	System.out.printf("Termina sort \n");

	// Revisa errores
	for(i=0;i<elems-1;i++)
		if(elem[i]>elem[i+1])
		{
			errores++;
			System.out.printf("elem[%d] = %d <--> elem[%d] = %d\n",i,elem[i],i+1,elem[i+1]);
		}

	System.out.printf("Errores %d\n",errores);
	System.out.println("El tiempo fue : " + ((after - before))/1000F + " segundos");
}

// A partir de aquí puedes modificar y agregar métodos

public static void sort(int []inicio, int elems)
{
    int cores=Runtime.getRuntime().availableProcessors();
    Thread[] hilos = new Worker[cores+1];
    int valdos=0;
    int value=0;
    for(int x = 0; x < (hilos.length-1); x++){
        valdos=value+(elems/cores);
        hilos[x]=new Worker(value,valdos);
        System.out.println("Creando hilo con val ini:"+value+" y val final "+valdos);
        hilos[x].start();
        value=valdos;
    }
    for (int x = 0; x < (hilos.length-1); x++) {
        try {
            hilos[x].join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Paralelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    valdos=0;
    value=0;
    Thread[] hilos2 = new Worker[(cores/2)+1];
    for(int x = 0; x < (hilos2.length-1); x++){
        valdos=value+(elems/(cores/2));
        hilos2[x]=new Worker(value,valdos);
        System.out.println("Creando hilo con val ini:"+value+" y val final "+valdos);
        hilos2[x].start();
        value=valdos;
    }
    for (int x = 0; x < (hilos2.length-1); x++) {
        try {
            hilos2[x].join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Paralelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    Thread unica=new Worker(0,elems);
    unica.start();
    try {
        unica.join();
    } catch (InterruptedException ex) {
        Logger.getLogger(Paralelo.class.getName()).log(Level.SEVERE, null, ex);
    }
}
}  //Fin de clase.