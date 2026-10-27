======================================================================
                  PROMPTZAL - ESPECIFICACIÓN DEL PROMPT FINAL
======================================================================

[ENCABEZADO GENERAL - DIRECTIVAS DECLARADAS]
  • @modelo            : "claude-sonnet-4-6"    // Especifica el modelo base de Inteligencia Artificial (ej. 'gpt-4', 'claude-3-5').
  • @rol               : "analista de datos"    // Define el rol o comportamiento global predeterminado de la IA.
  • @formato           : "markdown"             // Especifica el formato estructural de salida de las respuestas.

----------------------------------------------------------------------
[LISTADO DE AGENTES SEGÚN INSTRUCCIONES 'EJECUTAR']
----------------------------------------------------------------------

1. AGENTE: analista
   • Contexto: "Eres un analista de datos experto"
   • Variables locales:
       * ventas = CARGAR("ventas.csv")
   • Instrucciones en lenguaje natural:
       Paso 1: Basándote en ventas, responde: "Cuales son las 3 tendencias principales?". Guarda la respuesta como tendencias.
       Paso 2: Resume tendencias en un máximo de 100 palabras. Guarda el resultado como resumen.

----------------------------------------------------------------------
[RESULTADOS SOLICITADOS POR 'EXPORTAR']
----------------------------------------------------------------------
Al finalizar la ejecución, devuelve y consolida los siguientes resultados:
  • resumen            (fuente: Agente 'analista')

======================================================================
                     FIN DEL DOCUMENTO PROMPTZAL
======================================================================
