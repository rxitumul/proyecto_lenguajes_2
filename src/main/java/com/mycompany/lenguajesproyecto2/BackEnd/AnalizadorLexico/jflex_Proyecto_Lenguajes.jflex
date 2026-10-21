package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta.BibliotecaDeTokens;
import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas.ErrorLexico;

%%
%class PromptzalLexer
%public
%line
%column
%unicode
%type TokenEntidad

%{
    private List<TokenEntidad> tokensEncontrados = new ArrayList<>();
    private List<ErrorLexico> erroresLexicos = new ArrayList<>();
    private BibliotecaDeTokens biblioteca = new BibliotecaDeTokens();
    private boolean eofRegistrado = false;

    public BibliotecaDeTokens getBiblioteca() {
        return biblioteca;
    }

    public List<ErrorLexico> getErroresLexicos() {
        return erroresLexicos;
    }

    private TokenEntidad agregarToken(String tipo, String descripcion) {
        TokenEntidad nuevoToken = new TokenEntidad(tipo, descripcion, yyline + 1, yycolumn + 1);
        tokensEncontrados.add(nuevoToken);
        return nuevoToken;
    }

    public List<TokenEntidad> getListaDeTokens() {
        return tokensEncontrados;
    }
%}

Letra            = [a-zA-Z_]
Digito           = [0-9]
Identificador    = {Letra}({Letra}|{Digito})*
Directiva        = @{Identificador}
Espacio          = [ \t\r\n]+

// Símbolos compuestos y simples reconocidos dinámicamente desde BibliotecaDeTokens
SimboloCompuesto = "->" | "..." | "==" | "!=" | "<=" | ">=" | "&&" | "||" | "::" | "++" | "--"
SimboloSimple    = "{" | "}" | "(" | ")" | "=" | "+" | "," | ";" | ":" | "." | "<" | ">" | "!" | "?" | "-" | "*" | "/" | "%" | "&" | "|" | "^" | "~" | "#" | "$"

%%

{Espacio}       { /* Ignorar espacios */ }

"//".*          { /* Ignorar comentarios de una línea */ }
"/*" [^*] ~"*/" | "/*" "*"+ "/"  { /* Ignorar comentarios multilínea */ }

// Directivas (consultadas dinámicamente desde BibliotecaDeTokens)
{Directiva}     { 
    if (biblioteca.existeEnLosTokens(yytext())) {
        return agregarToken(biblioteca.obtenerTerminal(yytext()), yytext());
    }
    return agregarToken("DIRECTIVA", yytext()); 
}

// Cadenas simples
\"[^\"]*\"      { return agregarToken("CADENA", yytext()); }

// Palabras registradas / reservadas e Identificadores (consultadas desde BibliotecaDeTokens)
{Identificador} { 
    if (biblioteca.existeEnLosTokens(yytext())) {
        return agregarToken(biblioteca.obtenerTerminal(yytext()), yytext());
    } else {
        return agregarToken("ID", yytext());
    }
}

// Símbolos y operadores (consultados dinámicamente desde BibliotecaDeTokens)
{SimboloCompuesto} | {SimboloSimple} {
    if (biblioteca.existeEnLosTokens(yytext())) {
        return agregarToken(biblioteca.obtenerTerminal(yytext()), yytext());
    } else {
        System.err.println("Error Léxico: '" + yytext() + "' en línea " + (yyline+1) + ", col " + (yycolumn+1)); 
        erroresLexicos.add(new ErrorLexico(yytext(), "Carácter o símbolo no reconocido en el lenguaje Promptzal", yyline + 1, yycolumn + 1));
        return agregarToken("ERROR_LEXICO", yytext());
    }
}

// Números
{Digito}+"."{Digito}+ { return agregarToken("NUMERO", yytext()); }
{Digito}+             { return agregarToken("NUMERO", yytext()); }

// Fin de archivo — se registra el token EOF una sola vez y se retorna null
<<EOF>>         { 
    if (!eofRegistrado) {
        eofRegistrado = true;
        agregarToken("EOF", "EOF");
    }
    return null;
}

// Error léxico residual (cualquier otro caracter no capturado)
.               { 
                    System.err.println("Error Léxico: '" + yytext() + "' en línea " + (yyline+1) + ", col " + (yycolumn+1)); 
                    erroresLexicos.add(new ErrorLexico(yytext(), "Carácter no reconocido en el lenguaje Promptzal", yyline + 1, yycolumn + 1));
                    return agregarToken("ERROR_LEXICO", yytext());
                }