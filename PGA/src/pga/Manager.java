package pga;

import exceptions.EntidadNoAptaParaCursadaException;
import exceptions.NoEstaEntidadException;

import java.util.HashMap;
import java.util.Iterator;

public class Manager
{
    private static Manager instancia = null;
    // Las claves ser�n los nombres y apellidos de los alumnos
    private HashMap<String, HashMap<String, Alumno>> alumnos = new HashMap<String, HashMap<String, Alumno>>();
    // Las claves ser�n los nombres y apellidos de los profesores
    private HashMap<String, HashMap<String, Profesor>> profesores = new HashMap<String, HashMap<String, Profesor>>(); 
    // Las claves ser�n los nombres de las asignaturas
    private HashMap<String, HashMap<String, Asignatura>> asignaturas = new HashMap<String, HashMap<String, Asignatura>>(); 
    // Las claves ser�n los nombres de las cursadas
    private HashMap<String, HashMap<String, Cursada>> cursadas = new HashMap<String, HashMap<String, Cursada>>();

    private Manager()
    {
        super();
    }
    
    /**
     * Patr�n Singleton
     */
    public static Manager getInstancia()
    {
        if (instancia == null)
            instancia = new Manager();
        
        return instancia;
    }

    public void setAlumnos(HashMap<String, HashMap<String, Alumno>> alumnos)
    {
        this.alumnos = alumnos;
    }

    public HashMap<String, HashMap<String, Alumno>> getAlumnos()
    {
        return alumnos;
    }

    public void setProfesores(HashMap<String, HashMap<String, Profesor>> profesores)
    {
        this.profesores = profesores;
    }

    public HashMap<String, HashMap<String, Profesor>> getProfesores()
    {
        return profesores;
    }

    public void setAsignaturas(HashMap<String, HashMap<String, Asignatura>> asignaturas)
    {
        this.asignaturas = asignaturas;
    }

    public HashMap<String, HashMap<String, Asignatura>> getAsignaturas()
    {
        return asignaturas;
    }

    public void setCursadas(HashMap<String, HashMap<String, Cursada>> cursadas)
    {
        this.cursadas = cursadas;
    }

    public HashMap<String, HashMap<String, Cursada>> getCursadas()
    {
        return cursadas;
    }

    /*
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */

    public Alumno altaAlumno(String nombre, String apellido, String domicilio, String telefono, String mail) // RF01
    {
        Alumno alumno = (Alumno) Factory.getPersona(Factory.ALUMNO, nombre, apellido, domicilio, telefono, mail);
        String nombreCompleto = (alumno.getNombre() + alumno.getApellido()).toUpperCase(); // El hash est� en may�scula
        
        if (this.alumnos.containsKey(nombreCompleto))
            this.alumnos.get(nombreCompleto).put(alumno.getLegajo(), alumno); // Agregamos al HashMap el alumno cuya clave
                                                                              // ser� su legajo
        else
        {
            HashMap<String, Alumno> hash = new HashMap<String, Alumno>(); // Creamos una nueva cubeta y depositamos 
                                                                          // al alumno all�
            hash.put(alumno.getLegajo(), alumno);
            this.alumnos.put(nombreCompleto, hash);
        }
        return alumno;
    }
    
    public void bajaAlumno(Alumno alumno) throws NoEstaEntidadException // RF02
    {
        Iterator<HashMap<String, Cursada>> itH;
        Iterator<Cursada> itC;
        String nombreCompleto = (alumno.getNombre() + alumno.getApellido()).toUpperCase(); // El hash est� en may�scula
        
        if (this.alumnos.containsKey(nombreCompleto) && this.alumnos.get(nombreCompleto).remove(alumno.getLegajo(), alumno))
            // Si el alumno existe en la facultad y fue posible eliminarlo proseguimos a eliminarlo de todas las cursadas
        {
            if (this.alumnos.get(nombreCompleto).isEmpty()) // El HashMap ha quedado vac�o, sin alumnos
                this.alumnos.remove(nombreCompleto);
            
            // Eliminamos al alumno de todas las cursadas inscripto
            itH = this.cursadas.values().iterator();
            // La sentencia anterior nos devuelve un iterator de HashMap 
            while(itH.hasNext())
            {
                itC = itH.next().values().iterator();
                while (itC.hasNext())
                    itC.next().getAlumnos().remove(alumno.getLegajo()); // Se lo busca por legajo aqu�
                    // El remove puede dar T o F, logre eliminarlo o no seguimos avanzando.
            }
        }
        else
            throw new NoEstaEntidadException("Alumno no encontrado en el sistema.");
    }
    
    public void modificaAlumno(Alumno alumno, String nombre, String apellido, String domicilio, String telefono, 
                               String mail) throws NoEstaEntidadException // RF03
    {   
        String nombreViejo;
        String nombreNuevo;
        
        if (!alumno.getNombre().toUpperCase().equals(nombre.toUpperCase()) || !alumno.getApellido().toUpperCase().equals(apellido.toUpperCase()))
        {
            nombreViejo = (alumno.getNombre() + alumno.getApellido()).toUpperCase(); // El hash est� en may�scula
            
            if (this.alumnos.containsKey(nombreViejo) && this.alumnos.get(nombreViejo).remove(alumno.getLegajo(), alumno))
            {
                if (this.alumnos.get(nombreViejo).isEmpty()) // El HashMap ha quedado vac�o, sin alumnos
                    this.alumnos.remove(nombreViejo);
                
                alumno.setNombre(nombre);
                alumno.setApellido(apellido);
                nombreNuevo = (nombre + apellido).toUpperCase(); // El hash est� en may�scula
                
                if (this.alumnos.containsKey(nombreNuevo))
                    this.alumnos.get(nombreNuevo).put(alumno.getLegajo(), alumno); // Agregamos al HashMap el alumno
                else
                {
                    HashMap<String, Alumno> hash = new HashMap<String, Alumno>(); // Creamos una nueva cubeta y 
                                                                                  // depositamos al alumno all�
                    hash.put(alumno.getLegajo(), alumno);
                    this.alumnos.put(nombreNuevo, hash);
                }
            }
            else
                throw new NoEstaEntidadException("Alumno no encontrado en el sistema.");
        }
        
        alumno.setDomicilio(domicilio);
        alumno.setTelefono(telefono);
        alumno.setMail(mail);
    }
    
    public void modificaHistoriaAcademica(Alumno alumno, HashMap <String, Asignatura> historiaAcademica)
    {
        alumno.setHistoriaAcademica(historiaAcademica);
    }
    
    public HashMap<String, Alumno> ubicarAlumno(String nombre, String apellido) throws NoEstaEntidadException // RF05
    {
        HashMap<String, Alumno> ret = this.alumnos.get((nombre + apellido).toUpperCase()); // El hash est� en may�scula
        
        if(ret == null)
            throw new NoEstaEntidadException("Alumno no ubicado.");
        
        return ret;
    }
    
    public void altaAlumnoACursada(Alumno alumno, Cursada cursada) throws EntidadNoAptaParaCursadaException 
        // RF11, RF13
    {
        this.verificaAlumnoAptoParaCursada(alumno, cursada);
        cursada.getAlumnos().put(alumno.getLegajo(), alumno); // La clave del alumno ser� su legajo dentro de la Cursada
    }
                                
    public void bajaAlumnoDeCursada(Alumno alumno, Cursada cursada) throws NoEstaEntidadException // RF11, RF14
    {
        if(cursada.getAlumnos().remove(alumno.getLegajo()) == null)
            throw new NoEstaEntidadException("Alumno no encontrado en la cursada.");
    }
    
    /*
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */
    
    public void altaProfesor(String nombre, String apellido, String domicilio, String telefono, String mail) // RF01
    {
        Profesor profesor = (Profesor) Factory.getPersona(Factory.PROFESOR, nombre, apellido, domicilio, telefono, mail);
        String nombreCompleto = (profesor.getNombre() + profesor.getApellido()).toUpperCase(); // El hash est� en may�scula
        
        if (this.profesores.containsKey(nombreCompleto))
            this.profesores.get(nombreCompleto).put(profesor.getLegajo(), profesor); // Agregamos al HashMap el profesor 
                                                                                     // cuya clave ser� su legajo
        else
        {
            HashMap<String, Profesor> hash = new HashMap<String, Profesor>(); // Creamos una nueva cubeta y depositamos 
                                                                              // al profesor all�
            hash.put(profesor.getLegajo(), profesor);
            this.profesores.put(nombreCompleto, hash); 
        }
    }
    
    public void bajaProfesor(Profesor profesor) throws NoEstaEntidadException // RF02
    {
        Iterator<HashMap<String, Cursada>> itH;
        Iterator<Cursada> itC;
        String nombreCompleto = (profesor.getNombre() + profesor.getApellido()).toUpperCase(); // El hash est� en may�scula
        
        if (this.profesores.containsKey(nombreCompleto) && this.profesores.get(nombreCompleto).remove(profesor.getLegajo(), profesor))
            // Si el profesor existe en la facultad y fue posible eliminarlo proseguimos a eliminarlo de todas las cursadas
        {
            if (this.profesores.get(nombreCompleto).isEmpty()) // El HashMap ha quedado vac�o, sin profesores
                this.profesores.remove(nombreCompleto);
            
            // Eliminamos al profesor de todas las cursadas inscripto
            itH = this.cursadas.values().iterator();
            // La sentencia anterior nos devuelve un iterator de TreeSet 
            while(itH.hasNext())
            {
                itC = itH.next().values().iterator();
                while (itC.hasNext())
                    itC.next().getProfesores().remove(profesor.getLegajo()); // Se lo busca por legajo aqu�
                    // El remove puede dar T o F, logre eliminarlo o no seguimos avanzando.
            }
        }
        else
            throw new NoEstaEntidadException("Profesor no encontrado en el sistema.");
    }
    
    public void modificaProfesor(Profesor profesor, String nombre, String apellido, String domicilio, String telefono,
                                 String mail) throws NoEstaEntidadException // RF03
    {   
        String nombreViejo;
        String nombreNuevo;
        
        if (!profesor.getNombre().toUpperCase().equals(nombre.toUpperCase()) || !profesor.getApellido().toUpperCase().equals(apellido.toUpperCase()))
        {
            nombreViejo = (profesor.getNombre() + profesor.getApellido()).toUpperCase(); // El hash est� en may�scula
            
            if (this.profesores.containsKey(nombreViejo) && this.profesores.get(nombreViejo).remove(profesor.getLegajo(), profesor))
            {
                if (this.profesores.get(nombreViejo).isEmpty()) // El HashMap ha quedado vac�o, sin profesores
                    this.profesores.remove(nombreViejo);
                
                profesor.setNombre(nombre);
                profesor.setApellido(apellido);
                nombreNuevo = (nombre + apellido).toUpperCase(); // El hash est� en may�scula
                
                if (this.profesores.containsKey(nombreNuevo))
                    this.profesores.get(nombreNuevo).put(profesor.getLegajo(), profesor); // Agregamos al HashMap el profesor
                else
                {
                    HashMap<String, Profesor> hash = new HashMap<String, Profesor>(); // Creamos una nueva cubeta y 
                                                                                      // depositamos al profesor all�
                    hash.put(profesor.getLegajo(), profesor);
                    this.profesores.put(nombreNuevo, hash); 
                }
            }
            else
                throw new NoEstaEntidadException("Profesor no encontrado en el sistema.");
        }
        
        profesor.setDomicilio(domicilio);
        profesor.setTelefono(telefono);
        profesor.setMail(mail);  
    }

    public void modificaCompetencias(Profesor profesor, HashMap <String, Asignatura> competencias)
    {
        profesor.setCompetencias(competencias);
    }


    public HashMap<String, Profesor> ubicarProfesor(String nombre, String apellido) throws NoEstaEntidadException // RF05
    {
        HashMap<String, Profesor> ret = this.profesores.get((nombre + apellido).toUpperCase()); // El hash est� en may�scula
        
        if(ret == null)
            throw new NoEstaEntidadException("Profesor no ubicado.");
        
        return ret;
    }
    
    public void altaProfesorACursada(Profesor profesor, Cursada cursada) throws EntidadNoAptaParaCursadaException
        // RF12, RF15
    {
        this.verificaProfesorAptoParaCursada(profesor, cursada);
        cursada.getProfesores().put(profesor.getLegajo(), profesor); // La clave del profesor ser� su legajo dentro de 
                                                                     // la Cursada
    }
                                
    public void bajaProfesorDeCursada(Profesor profesor, Cursada cursada) throws NoEstaEntidadException // RF12, RF16
    {
        String nombreCursadaMayus;
        
        if(cursada.getProfesores().remove(profesor.getLegajo()) == null)
            throw new NoEstaEntidadException("Profesor no encontrado en la cursada.");
        
        if (cursada.getProfesores().isEmpty()) // Si la cursada ha quedado sin profesores se la debe eliminar
        {
            nombreCursadaMayus = cursada.getNombre().toUpperCase();  // El hash est� en may�scula
            this.cursadas.get(nombreCursadaMayus).remove(cursada);
            if (this.cursadas.get(nombreCursadaMayus).isEmpty()) // Si hemos dejado al HashMap sin cursadas eliminamos
                                                                 // tambi�n al HashMap y a su clave del HashMap de 
                                                                 // cursadas
                this.cursadas.remove(nombreCursadaMayus);
        }
        // Si esta �ltima parte es correcta, �qu� sucede cuando se elimina un profesor del sistema? �Se verifica que
        // la cursada no haya quedado sin profesores? Si es as�, �se la elimina?
    }
    
    /*
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */
    
    public void altaAsignatura(String nombre, HashMap<String, Asignatura> correlatividades) // RF01
    {
        String nombreAsignaturaMayus = nombre.toUpperCase(); // El hash est� en may�scula
        
        Asignatura asignatura = Factory.getAsignatura(nombreAsignaturaMayus, correlatividades);
        
        if (this.asignaturas.containsKey(nombreAsignaturaMayus))
            this.asignaturas.get(nombreAsignaturaMayus).put(asignatura.getId(), asignatura); // Agregamos al HashMap la asignatura
        else
        {
            HashMap<String, Asignatura> hash = new HashMap<String, Asignatura>(); // Creamos una nueva cubeta y 
                                                                                  // depositamos a la asignatura all�
            hash.put(asignatura.getId(), asignatura);
            this.asignaturas.put(nombreAsignaturaMayus, hash); 
        }
    }
    
    public void bajaAsignatura(Asignatura asignatura) throws NoEstaEntidadException // RF02
    {
        Iterator<HashMap<String, Cursada>> itH;
        Iterator<Cursada> itC;
        Cursada cursada;
        String nombreAsignaturaMayus = asignatura.getNombre(); // El hash est� en may�scula
        String nombreCursadaMayus;
        
        if (this.asignaturas.containsKey(nombreAsignaturaMayus) && this.asignaturas.get(nombreAsignaturaMayus).remove(asignatura.getId(), asignatura))
            // Si hemos encontrado la asignatura y podido eliminarla
        {   
            if (this.asignaturas.get(nombreAsignaturaMayus).isEmpty()) // Si el HashMap de las asignaturas ha quedado
                                                                        // vac�o lo eliminamos del HashMap general de 
                                                                        // asignaturas
                this.asignaturas.remove(nombreAsignaturaMayus);
            
            // Eliminamos todas las cursadas en las que la asignatura aparece
            itH = this.cursadas.values().iterator();
            while(itH.hasNext())
            {
                itC = itH.next().values().iterator();
                while (itC.hasNext())
                {
                    cursada = itC.next();
                    if (cursada.getAsignatura().equals(asignatura))
                    {
                        nombreCursadaMayus = cursada.getNombre().toUpperCase(); // El hash est� en may�scula
                        this.cursadas.get(nombreCursadaMayus).remove(cursada); // Eliminamos la cursada de su HashMap
                        if (this.cursadas.get(nombreCursadaMayus).isEmpty()) // Si el HashMap queda vac�o se lo debe
                                                                             // eliminar del HashMap general de cursadas
                            this.cursadas.remove(nombreCursadaMayus);
                    }
                }
            }
        }
        else
            throw new NoEstaEntidadException("Asignatura no encontrada en el sistema.");
    }
    
    public void modificaAsignatura(Asignatura asignatura, String nombre)
        throws NoEstaEntidadException // RF03
    {
        String nombreAsignaturaMayus = asignatura.getNombre(); // El hash est� en may�scula
        if (!nombreAsignaturaMayus.equals(nombre.toUpperCase()))
        {            
            if (this.asignaturas.containsKey(nombreAsignaturaMayus) && this.asignaturas.get(nombreAsignaturaMayus).remove(asignatura.getId(), asignatura))
            {
                if (this.asignaturas.get(nombreAsignaturaMayus).isEmpty()) // El HashMpa ha quedado vac�o, sin asignaturas
                    this.asignaturas.remove(nombreAsignaturaMayus);
                
                asignatura.setNombre(nombre);
                nombreAsignaturaMayus = nombre.toUpperCase(); // El hash est� en may�scula
                
                if (this.asignaturas.containsKey(nombreAsignaturaMayus))
                    this.asignaturas.get(nombreAsignaturaMayus).put(asignatura.getId(), asignatura); // Agregamos al HashMap la asignatura
                else
                {
                    HashMap<String, Asignatura> hash = new HashMap<String, Asignatura>(); // Creamos una nueva cubeta y 
                                                                                          // depositamos a la
                                                                                          // asignatura all�
                    hash.put(asignatura.getId(), asignatura);
                    this.asignaturas.put(nombreAsignaturaMayus, hash); 
                }
            }
            else
                throw new NoEstaEntidadException("Asignatura no encontrada en el sistema.");
        }
    }
    
    public void modificaCorrelatividades(Asignatura asignatura, HashMap <String, Asignatura> correlatividades)
    {
        asignatura.setCorrelatividades(correlatividades);
    }
    
    public HashMap<String, Asignatura> ubicarAsignatura(String nombre) throws NoEstaEntidadException // RF05
    {
        HashMap<String, Asignatura> ret = this.asignaturas.get(nombre.toUpperCase()); // El hash est� en may�scula
        
        if(ret == null)
            throw new NoEstaEntidadException("Asignatura no ubicada.");
        
        return ret;
    }
    
    /*
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */

    public void altaCursada(String nombre, Asignatura asignatura, String periodo, String dia, String horaInicio, 
                            String horaFin) // RF06
    {
        /**
         * El requisito de verificar que la cursada sea viable (RF17) no tiene sentido ya que podr�an existir dos 
         * cursadas, incluso de la misma asignatura, en el mismo d�a y horario.
         */
        String nombreCursadaMayus = nombre.toUpperCase(); // El hash est� en may�scula
        Cursada cursada = Factory.getCursada(nombre, asignatura, periodo, dia, horaInicio, horaFin);
        
        if (this.cursadas.containsKey(nombreCursadaMayus))
            this.cursadas.get(nombreCursadaMayus).put(cursada.getId(), cursada); // Agregamos al HashMap la cursada
        else
        {
            HashMap<String, Cursada> hash = new HashMap<String, Cursada>(); // Creamos una nueva cubeta y depositamos
                                                                            // a la cursada all�
            hash.put(cursada.getId(), cursada);
            this.cursadas.put(nombreCursadaMayus, hash); 
        }
    }
    
    public void bajaCursada(Cursada cursada) throws NoEstaEntidadException // RF07
    {        
        String nombreCursadaMayus = cursada.getNombre(); // El hash est� en may�scula
        
        if (this.cursadas.containsKey(nombreCursadaMayus) && this.cursadas.get(nombreCursadaMayus).remove(cursada.getId(), cursada))
            // Si hemos encontrado la cursada y podido eliminarla
            if (this.cursadas.get(nombreCursadaMayus).isEmpty()) // Si el HashMap de las cursadas ha quedado vac�o
                                                                  // lo eliminamos del HashMap general de cursadas
                this.cursadas.remove(nombreCursadaMayus);
        else
            throw new NoEstaEntidadException("Cursada no encontrada en el sistema.");
    }
    
    public void modificaCursada(Cursada cursada, String nombre, Asignatura asignatura, String periodo, String dia, 
                                String horaInicio, String horaFin) throws NoEstaEntidadException //RF08
    {
        String nombreCursadaMayus = cursada.getNombre().toUpperCase(); // El hash est� en may�scula
        
        if (!nombreCursadaMayus.equals(nombre.toUpperCase()))
        {            
            if (this.cursadas.containsKey(nombreCursadaMayus) && this.cursadas.get(nombreCursadaMayus).remove(cursada.getId(), cursada))
            {
                if (this.cursadas.get(nombreCursadaMayus).isEmpty()) // El HashMap ha quedado vac�o, sin cursadas
                    this.cursadas.remove(nombreCursadaMayus);
                
                cursada.setNombre(nombre);
                nombreCursadaMayus = nombre.toUpperCase(); // El hash est� en may�scula
                
                if (this.cursadas.containsKey(nombreCursadaMayus))
                    this.cursadas.get(nombreCursadaMayus).put(cursada.getId(), cursada); // Agregamos al HashMap la cursada
                else
                {
                    HashMap<String, Cursada> hash = new HashMap<String, Cursada>(); // Creamos una nueva cubeta y 
                                                                                    // depositamos a la cursada all�
                    hash.put(cursada.getId(), cursada);
                    this.cursadas.put(nombreCursadaMayus, hash); 
                }
            }
            else
                throw new NoEstaEntidadException("Cursada no encontrada en el sistema.");
        }
        
        cursada.setAsignatura(asignatura);
        cursada.setPeriodo(periodo);
        cursada.setDia(dia);
        cursada.setHoraInicio(horaInicio);
        cursada.setHoraFin(horaFin);   
    }
    
    public HashMap<String, Cursada> ubicarCursada(String nombre) throws NoEstaEntidadException // RF05
    {
        HashMap<String, Cursada> ret = this.cursadas.get(nombre.toUpperCase()); // El hash est� en may�scula
        
        if(ret == null)
            throw new NoEstaEntidadException("Cursada no ubicada.");
        
        return ret;
    }
    
    /*
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */
    
    public boolean horariosSolapan(Cursada c1, Cursada c2)
    {
        return c1.getDia().equals(c2.getDia()) && ((c1.getHoraInicio().compareTo(c2.getHoraInicio()) >= 0 && 
                                                    c1.getHoraInicio().compareTo(c2.getHoraFin()) <= 0) || 
                                                   (c1.getHoraFin().compareTo(c2.getHoraInicio()) >= 0 && 
                                                    c1.getHoraFin().compareTo(c2.getHoraFin()) <= 0)); 
    }
    
    public void verificaAlumnoAptoParaCursada(Alumno alumno, Cursada cursada) throws EntidadNoAptaParaCursadaException
    {
        this.verificaAlumnoHabilitado(alumno, cursada);
        this.verificaAlumnoOcupado(alumno, cursada);
    }
    
    public void verificaAlumnoHabilitado(Alumno alumno, Cursada cursada) throws EntidadNoAptaParaCursadaException
        // RF19
    {
        Iterator<Asignatura> it;
        boolean sigue = true;
        
        it = cursada.getAsignatura().getCorrelatividades().values().iterator();
        while(it.hasNext() && sigue)
            sigue = alumno.getHistoriaAcademica().containsKey(it.next().getId()); // La clave de la asignatura es su ID
        
        if(!sigue)
            throw new EntidadNoAptaParaCursadaException("El alumno no cumple con las correlatividades para inscribirse" +
                "a la cursada.");
    }
    
    public void verificaAlumnoOcupado(Alumno alumno, Cursada cursada) throws EntidadNoAptaParaCursadaException
        // RF21
    {
        Iterator<HashMap<String, Cursada>> itH;
        Iterator<Cursada> itC;
        Cursada c;
        boolean sigue = true;
        
        itH = this.cursadas.values().iterator();
        while (itH.hasNext() && sigue)
        {
            itC = itH.next().values().iterator();
            while(itC.hasNext() && sigue)
            {
                c = itC.next();
                if(c.getAlumnos().containsKey(alumno.getLegajo()))
                    sigue = !this.horariosSolapan(cursada, c);
            }
        }
        
        if(!sigue)
            throw new EntidadNoAptaParaCursadaException("El alumno no cumple con las franjas horarias para inscribirse" +
                "a la cursada.");
    }
    
    public void verificaProfesorAptoParaCursada(Profesor profesor, Cursada cursada) throws EntidadNoAptaParaCursadaException
    {
        this.verificaProfesorHabilitado(profesor, cursada);
        this.verificaProfesorOcupado(profesor, cursada);
    }
            
    public void verificaProfesorHabilitado(Profesor profesor, Cursada cursada) throws EntidadNoAptaParaCursadaException
        // RF18
    {
        if(!profesor.getCompetencias().containsKey(cursada.getAsignatura().getId()))
                throw new EntidadNoAptaParaCursadaException("El profesor no est� habilitado para dictar la cursada.");

    }
    public void verificaProfesorOcupado(Profesor profesor, Cursada cursada) throws EntidadNoAptaParaCursadaException
        // RF20
    {        
        Iterator<HashMap<String, Cursada>> itH;
        Iterator<Cursada> itC;
        Cursada c;
        boolean sigue = true;
        
        itH = this.cursadas.values().iterator();
        while (itH.hasNext() && sigue)
        {
            itC = itH.next().values().iterator();
            while(itC.hasNext() && sigue)
            {
                c = itC.next();
                if(c.getProfesores().containsKey(profesor.getLegajo()))
                    sigue = !this.horariosSolapan(cursada, c);
            }
        }
            
        if(!sigue)
            throw new EntidadNoAptaParaCursadaException("El profesor no cumple con las franjas horarias para inscribirse" +
                "a la cursada.");
    }
}
