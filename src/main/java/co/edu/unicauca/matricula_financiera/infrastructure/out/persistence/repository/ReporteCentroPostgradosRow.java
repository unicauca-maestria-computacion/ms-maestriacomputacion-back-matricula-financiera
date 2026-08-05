package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository;

public class ReporteCentroPostgradosRow {
    private String identificacion;
    private String nombreCompleto;
    private double valorMatriculaSMMLV;
    private int semestreFinanciero;
    private boolean aplicaDescuentoVoto;
    private boolean aplicaDescuentoEgresado;
    private String resolucionBeca;
    private Double porcentajeBeca;
    private int semestreAcademico;
    private String materia;
    private String docente;
    private String grupo;

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public double getValorMatriculaSMMLV() { return valorMatriculaSMMLV; }
    public void setValorMatriculaSMMLV(double valorMatriculaSMMLV) { this.valorMatriculaSMMLV = valorMatriculaSMMLV; }
    public int getSemestreFinanciero() { return semestreFinanciero; }
    public void setSemestreFinanciero(int semestreFinanciero) { this.semestreFinanciero = semestreFinanciero; }
    public boolean isAplicaDescuentoVoto() { return aplicaDescuentoVoto; }
    public void setAplicaDescuentoVoto(boolean aplicaDescuentoVoto) { this.aplicaDescuentoVoto = aplicaDescuentoVoto; }
    public boolean isAplicaDescuentoEgresado() { return aplicaDescuentoEgresado; }
    public void setAplicaDescuentoEgresado(boolean aplicaDescuentoEgresado) { this.aplicaDescuentoEgresado = aplicaDescuentoEgresado; }
    public String getResolucionBeca() { return resolucionBeca; }
    public void setResolucionBeca(String resolucionBeca) { this.resolucionBeca = resolucionBeca; }
    public Double getPorcentajeBeca() { return porcentajeBeca; }
    public void setPorcentajeBeca(Double porcentajeBeca) { this.porcentajeBeca = porcentajeBeca; }
    public int getSemestreAcademico() { return semestreAcademico; }
    public void setSemestreAcademico(int semestreAcademico) { this.semestreAcademico = semestreAcademico; }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
}
