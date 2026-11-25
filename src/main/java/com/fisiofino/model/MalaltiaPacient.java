package com.fisiofino.model;

public class MalaltiaPacient {

    private String dniPacient;
    private String code;
    private String name;
    private String status;
    private String treatment;

    public String getDniPacient() {
        return dniPacient;
    }

    public void setDniPacient(String dniPacient) {
        this.dniPacient = dniPacient;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    @Override
    public String toString() {
        return "MalaltiaPacient [dniPacient=" + dniPacient + ", code=" + code + ", name=" + name
                + ", status=" + status + ", treatment=" + treatment + "]";
    }
}
