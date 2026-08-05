package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import java.util.List;

public class ReporteCentroPostgradosResponse {

    public static class MateriaReporte {
        private String codigoOid;
        private String materia;
        public MateriaReporte() {}
        public MateriaReporte(String codigoOid, String materia) { this.codigoOid = codigoOid; this.materia = materia; }
        public String getCodigoOid() { return codigoOid; }
        public void setCodigoOid(String codigoOid) { this.codigoOid = codigoOid; }
        public String getMateria() { return materia; }
        public void setMateria(String materia) { this.materia = materia; }
    }

    private String identificacion;
    private String nombreCompleto;
    private double valorMatriculaSMMLV;
    private int semestreFinanciero;
    private boolean aplicaDescuentoVoto;
    private boolean aplicaDescuentoEgresado;
    private String resolucionBeca;
    private Double porcentajeBeca;
    private int semestreAcademico;
    private List<MateriaReporte> materias;
    private String docenteEncargado;
    private String grupoClase;

    public ReporteCentroPostgradosResponse() {}

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
    public List<MateriaReporte> getMaterias() { return materias; }
    public void setMaterias(List<MateriaReporte> materias) { this.materias = materias; }
    public String getDocenteEncargado() { return docenteEncargado; }
    public void setDocenteEncargado(String docenteEncargado) { this.docenteEncargado = docenteEncargado; }
    public String getGrupoClase() { return grupoClase; }
    public void setGrupoClase(String grupoClase) { this.grupoClase = grupoClase; }
}
