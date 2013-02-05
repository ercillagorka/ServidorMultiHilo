
import java.io.*;
import java.net.*;
import java.util.Scanner;
/**
 * @author gorkaercilla
 */
public class Servidor {

    public static void main(String[] args) {
       
        try {
  // 1.CREACION DEL SOCKET-----------------------------------------
         //variables para gestionar el numero de conexiones el servidor   
            int i=0,maxConnections=0;   
         //creacion del socket para que escuche en el puerto TCP/6000   
            final ServerSocket SocketS= new ServerSocket(6000);
  
  // 2.WHILE<MAXCONNECTIONS-----------------------------------------
         //Mientras el numero de conexiones sea menor que el numero maximo   
            while ((i++<maxConnections)||(maxConnections==0)) 
            {  
  
  // 3.ESCUCHAR CLIENTES--------------------------------------------  
         //Escuchar peticion del cliente       
            final Socket socketC=SocketS.accept();
                
  // 4.CREACION DEL HILO--------------------------------------------
         // Una vez que llega un clienten se define el hilo y se arranca        
            Thread t=new Thread()
            {
                public void run()
                {
          
  // 5.DECLARACION DE VARIABLES Y FLUJOS DE ENTRADA Y SALIDA--------                        
                 try {
                   //Usuario recibido por socket   
                          String usulogin;
                  //password recibido por el socket   
                          String passlogin;
                  //usuario recogido del fichero acceso   
                          String usuario;
                  //password recogido del fichero acceso   
                          String password;
                  //variable para controlar si el login es correcto   
                          boolean loginok=false;
                  //cadena de entrada del fichero
                          String cadena="";
                  //mostrar mensaje de recepcion del cliente   
                          System.out.println("Escuchado cliente "+socketC.getInetAddress());

                  //Declaracion de las variables de entrada y salida del socket
                          OutputStream os=socketC.getOutputStream();
                          DataOutputStream dos=new DataOutputStream(os);                            
                          InputStream is=socketC.getInputStream();
                          DataInputStream isr=new DataInputStream(is);
                  //variable que empleamos para almacenar lo que recibimos por el socket
                          String entrada;
                  //variable que guarda el comando recibido por el socket       
                          String comando="";
                  //variable que guarda el aprametro recibido por el socket
                          String parametro;

                  //enviar mensaje de bienvenida por el socket al cliente
                          dos.writeUTF("Bienvenido al servidor\n\r");

 // 6.WHILE COMMANDO <> QUIT----------------------------------------
                  // mientras el comando sea distinto de QUIT                
                          while (!comando.equals("QUIT")) 
                          {  
                     //inicializar login a false. si el login es ok,mas adelante se activara       
                              loginok=false;

// 7.SI COMANDO == USER --> usulogin=parameter---------------------
                     //recibir texto del cliente por el socket
                              entrada=isr.readLine();
                     //si el texto recibido acaba en espacio, enviar mensaje de opcion incorrecta
                              if (entrada.endsWith(" ")) 
                              {
                                  dos.writeUTF(" Opcion Incorrecta: Falta parametro\n\r");
                              }
                              else
                              {
                     //extraer del texto recibido el comando y parametro
                     //mediante el scaneo de tokens empleando el separador 'espacio'
                                  Scanner scanner=new Scanner(entrada);
                                  scanner.useDelimiter(" ");
                                  comando=scanner.next();
                     //si la entrada contiene un espacio             
                                  if ((entrada.contains(" "))) 
                                  {
                         //meter en parametro el siguiente token
                                      parametro=scanner.next();
                         //si el comando es USER  
                                      if (comando.equals("USER")) 
                                      {
                             //meter en usulogin lo que se ha pasado como parametro
                                          usulogin=parametro;
                             //enviar mensaje de Usuario Recibido
                                          dos.writeUTF("Usuario Recibido\n\r");

 //8.SI COMANDO==PASS --> passlogin= <parametro> -----------------------
                             //esperar a recibir otro texto del cliente             
                                          entrada=isr.readLine();
                             //si el texto recibido contiene
                                          if ((entrada.contains(" "))) 
                                          {
                                  //extraer del texto el comando y el parametro
                                              Scanner scanner2=new Scanner(entrada);
                                              scanner2.useDelimiter(" ");
                                              comando=scanner2.next();
                                              parametro=scanner2.next();
                                  //si el comando es PASS
                                              if (comando.equals("PASS")) 
                                              {
                                        //meter en passlogin el parametro recibido por el cliente
                                                  passlogin =parametro;

 // 9.COMPROBACION DEL LOGIN ------------------------------------------
                                        //abrir el fichero de acceso donde tenemos los usuarios y contraseñas
                                                  BufferedReader fEntrada=new BufferedReader(new FileReader("acceso"));
                                        //leer todas las lineas del fichero acceso
                                                  while ((cadena=fEntrada.readLine())!=null) 
                                                  {
                                            //sacar los usuarios y password del fichero acceso
                                                      Scanner scanner3=new Scanner(cadena);
                                                      scanner3.useDelimiter(";");
                                                      usuario=scanner3.next();
                                                      password=scanner3.next();
                                            //si el usuario y contraseña recibidos por el socket es igual al del fichero...
                                                      if ((usulogin.equals(usuario))&&(passlogin.equals(password))) 
                                                      {
                                                  //activar login ok     
                                                          loginok=true;
                                                      }

                                                  }
                                        //cerrar el fichero acceso
                                                  fEntrada.close();

 // 10.LOGIN OK / LOGIN FALLO--------------------------------------------
                                        //si el login es valido
                                                  if (loginok) 
                                                  {
                                            //enviar por el socket login Correcto
                                                      dos.writeUTF("Login Correcto\n\r");
                                                      //recibir texto del cliente por el socket
                                                        entrada=isr.readLine();
                                                      //si la entrada es LIST  
                                                        if (entrada.equals("LIST")) {
                                                            //abrir el fichero de acceso donde tenemos los usuarios y contraseñas
                                                            fEntrada = new BufferedReader(new FileReader("acceso"));
                                                            while ((cadena = fEntrada.readLine()) != null) {
                                                                //sacar los usuarios y password del fichero acceso y mostrarlos
                                                                Scanner scanner3 = new Scanner(cadena);
                                                                scanner3.useDelimiter(";");
                                                                dos.writeUTF("USER: " + scanner3.next() + " Pass: " + scanner3.next()+"\n\r");
                                                            }
                                                            //cerrar el fichero acceso
                                                            fEntrada.close();
                                                        }else
                                                        {
                                                            dos.writeUTF("Comando no encontrado\n\r");
                                                        }
                                                  }
                                                  else
                                                  {
                                            //si no.. enviar por el socket login Fallo
                                                      dos.writeUTF("Login Fallo\n\r");
                                                  }
                                              }
                                  //si el comando recibido es distinto de USER enviar mensaje de error            
                                            }  else
                                              {
                                                  dos.writeUTF("Opcion Incorrecta.Vuelva a repetir el login con el comando USER\n");
                                              }
                                          }
                             //si el comando recibido es distinto de USER enviar mensaje de error             
                                          else
                                          {
                                              dos.writeUTF("Opcion Incorrecta: Comando invalido. Teclear USER\n");
                                          }
                                      }
                                //si el comando recibido es distinto de USER enviar mensaje de error             
                                    else
                                    {
                                        dos.writeUTF("Opcion Incorrecta: Comando invalido. Teclear USER\n");
                                    }
                                  
                              }
                          }
   // 11. CERRAR SOCKET CLIENTE---------------------------------------------
                            socketC.close();
  
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                };
   // 12. EJECUTAR EL HILO ------------------------------------------------
         //una vez definido el hilo, arrancarlo
                t.start();
                
            }
         /*
          * cuando lleguemos al maximo de conexiones de clientes,
          * saldremos del bucle y cerramos el socket del servidor
          */   
            SocketS.close();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}
