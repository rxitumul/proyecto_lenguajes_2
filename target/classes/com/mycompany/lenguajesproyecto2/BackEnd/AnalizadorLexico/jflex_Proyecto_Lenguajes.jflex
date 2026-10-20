package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

%%
%class PromptzalLexer
%public
%debug
%line
%column
%unicode
%type TokenEntidad

%{
    private List<TokenEntidad> tokensEncontrados = new ArrayList<>();

    // Modificado: Agrega a la lista Y ADEMÁS retorna el token creado
    private TokenEntidad agregarToken(String tipo, String descripcion) {
        TokenEntidad nuevoToken = new TokenEntidad(tipo, descripcion, yyline + 1, yycolumn + 1);
        tokensEncontrados.add(nuevoToken);
        return nuevoToken;
    }

    public List<TokenEntidad> getListaDeTokens() {
        return tokensEncontrados;
    }
%}

Letra         = [a-zA-Z_]
Digito        = [0-9]
Identificador = {Letra}({Letra}|{Digito})*
Directiva     = @{Identificador}
Espacio       = [ \t\r\n]+

%%

{Espacio}       { /* Ignorar espacios */ }

"//".*          { /* Ignorar comentarios de una línea */ }
"/*" [^*] ~"*/" | "/*" "*"+ "/"  { /* Ignorar comentarios multilínea */ }

// Directivas
{Directiva}     { return agregarToken("DIRECTIVA", yytext()); }

// Palabras Reservadas
"AGENTE"        { return agregarToken("AGENTE", yytext()); }
"contexto"      { return agregarToken("CONTEXTO", yytext()); }
"variable"      { return agregarToken("VARIABLE", yytext()); }
"EJECUTAR"      { return agregarToken("EJECUTAR", yytext()); }
"EXPORTAR"      { return agregarToken("EXPORTAR", yytext()); }
"CARGAR"        { return agregarToken("CARGAR", yytext()); }

// Comandos de IA
"PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER" 
                { return agregarToken("COMANDO_IA", yytext()); }

// Conectores
"SOBRE" | "DESDE" | "EN" | "COMO" 
                { return agregarToken("CONECTOR", yytext()); }

// Símbolos
"->"            { return agregarToken("FLECHA", yytext()); }
"{"             { return agregarToken("LLAVE_A", yytext()); }
"}"             { return agregarToken("LLAVE_C", yytext()); }
"("             { return agregarToken("PAR_A", yytext()); }
")"             { return agregarToken("PAR_C", yytext()); }
"="             { return agregarToken("IGUAL", yytext()); }
"+"             { return agregarToken("MAS", yytext()); }
","             { return agregarToken("COMA", yytext()); }

// Cadenas simples
\"[^\"]*\"      { return agregarToken("CADENA", yytext()); }

// Identificadores
{Identificador} { return agregarToken("ID", yytext()); }

// Números
{Digito}+"."{Digito}+ { return agregarToken("NUMERO", yytext()); }
{Digito}+             { return agregarToken("NUMERO", yytext()); }

// Fin de archivo
<<EOF>>         { return agregarToken("EOF", "EOF"); }

// Error léxico
.               { 
                    System.err.println("Error Léxico: '" + yytext() + "' en línea " + (yyline+1) + ", col " + (yycolumn+1)); 
                    return null; 
                }