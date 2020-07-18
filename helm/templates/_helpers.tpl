{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "mp-api-gateway.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "mp-api-gateway.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "mp-api-gateway.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "mp-api-gateway.labels" -}}
helm.sh/chart: {{ include "mp-api-gateway.chart" . }}
{{ include "mp-api-gateway.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "mp-api-gateway.selectorLabels" -}}
app.kubernetes.io/name: {{ include "mp-api-gateway.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}


{{- define "mpKafkaRestProxyUrl" }}
{{- printf "http://%s-mp-kafka-rest-proxy:%s" ( required "A valid .Values.global.namespaceName entry required!" .Values.global.namespaceName) (.Values.global.mpKafkaRestProxyPort | toString) | quote}}
{{- end }}

{{- define "bcClientURL" }}
{{- printf "http://%s-mp-bc-client:%s" ( required "A valid .Values.global.namespaceName entry required!" .Values.global.namespaceName) (.Values.global.mpBCClientPort | toString) | quote}}
{{- end }}

{{- define "entityManagerURL" }}
{{- printf "http://%s-mp-entity-manager:%s" ( required "A valid .Values.global.namespaceName entry required!" .Values.global.namespaceName) (.Values.global.mpEntityManagerPort | toString) | quote}}
{{- end }}

{{- define "signatureVerifierURL" }}
{{- printf "http://%s-mp-signature-verifier:%s" ( required "A valid .Values.global.namespaceName entry required!" .Values.global.namespaceName) (.Values.global.mpSignatureVerifierPort | toString) | quote}}
{{- end }}