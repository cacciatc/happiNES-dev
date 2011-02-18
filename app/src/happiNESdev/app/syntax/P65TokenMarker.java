/*
 * P65TokenMarker.java - P65 token marker
 * Copyright (C) 2011 Chris Cacciatore
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package happiNESdev.app.syntax;

import javax.swing.text.Segment;

import processing.app.syntax.KeywordMap;
import processing.app.syntax.Token;
import processing.app.syntax.TokenMarker;

/**
 * P65 token marker.
 *
 * @author Chris Cacciatore
 * @version $Id: P65TokenMarker.java
 */
public class P65TokenMarker extends TokenMarker
{
        public P65TokenMarker()
        {
                this(getKeywords());
        }

        public P65TokenMarker(KeywordMap keywords)
        {
                this.keywords = keywords;
        }

        public byte markTokensImpl(byte token, Segment line, int lineIndex)
        {
                char[] array = line.array;
                int offset = line.offset;
                lastOffset = offset;
                lastKeyword = offset;
                int mlength = line.count + offset;
                boolean backslash = false;

loop:           for(int i = offset; i < mlength; i++)
                {
                        int i1 = (i+1);

                        char c = array[i];
                        if(c == '\\')
                        {
                                backslash = !backslash;
                                continue;
                        }

                        switch(token)
                        {
                        case Token.NULL:
                                switch(c)
                                {
                                case '#':
                                        if(backslash)
                                                backslash = false;
                                        break;
                                case '"':
                                        doKeyword(line,i,c);
                                        if(backslash)
                                                backslash = false;
                                        else
                                        {
                                                addToken(i - lastOffset,token);
                                                token = Token.LITERAL1;
                                                lastOffset = lastKeyword = i;
                                        }
                                        break;
                                case '.':
                                   
                                    doKeyword(line,i,c);
                                    if(backslash)
                                            backslash = false;
                                    else
                                    {
                                            addToken(i - lastOffset,token);
                                            token = Token.KEYWORD3;
                                            lastOffset = lastKeyword = i;
                                    }
                                    break;
                                case '_':
                                    doKeyword(line,i,c);
                                    if(backslash)
                                            backslash = false;
                                    else
                                    {
                                            addToken(i - lastOffset,token);
                                            token = Token.LABEL;
                                            lastOffset = lastKeyword = i;
                                    }
                                    break;
                                case ':':
                                        if(lastKeyword == offset)
                                        {
                                                if(doKeyword(line,i,c))
                                                        break;
                                                backslash = false;
                                                addToken(i1 - lastOffset,Token.LABEL);
                                                lastOffset = lastKeyword = i1;
                                        }
                                        else if(doKeyword(line,i,c))
                                                break;
                                        break;
                                case ';':
                                        backslash = false;
                                        doKeyword(line,i,c);
                                        if(mlength - i > 1)
                                        {
                                                        addToken(i - lastOffset,token);
                                                        addToken(mlength - i,Token.COMMENT1);
                                                        lastOffset = lastKeyword = mlength;
                                                        break loop;
                                        }
                                        break;
                                
                                default:
                                        backslash = false;
                                        if(!Character.isLetterOrDigit(c))
                                                doKeyword(line,i,c);
                                        break;
                                }
                                break;
                        case Token.COMMENT1:
                        case Token.COMMENT2:
                                backslash = false;
                                if(c == '*' && mlength - i > 1)
                                {
                                        if(array[i1] == '/')
                                        {
                                                i++;
                                                addToken((i+1) - lastOffset,token);
                                                token = Token.NULL;
                                                lastOffset = lastKeyword = i+1;
                                        }
                                }
                                break;
                        case Token.LABEL:
                        	if(backslash)
                                backslash = false;
	                        else if(c == ' ' || c == '\n' || c == '\t')
	                        {
	                                addToken(i1 - lastOffset,token);
	                                token = Token.NULL;
	                                lastOffset = lastKeyword = i1;
	                        }
	                        break;
                        case Token.LITERAL1:
                                if(backslash)
                                        backslash = false;
                                else if(c == '"')
                                {
                                        addToken(i1 - lastOffset,token);
                                        token = Token.NULL;
                                        lastOffset = lastKeyword = i1;
                                }
                                break;
                        case Token.KEYWORD3:
                        	
                            if(backslash)
                                    backslash = false;
	                        else if(c == ' ' || i == mlength-1 || c == '\t')
                            {
                                    addToken(i1 - lastOffset,token);
                                    token = Token.NULL;
                                    lastOffset = lastKeyword = i1;
                            }
                            break;
                        case Token.LITERAL2:
                                if(backslash)
                                        backslash = false;
    	                        else if(c == ' ' || c == '\n' || c == '\t')
                                {
                                        addToken(i1 - lastOffset,Token.LITERAL1);
                                        token = Token.NULL;
                                        lastOffset = lastKeyword = i1;
                                }
                                break;
                        default:
                                throw new InternalError("Invalid state: "
                                        + token);
                        }
                }

                if(token == Token.NULL)
                        doKeyword(line,mlength,'\0');

                switch(token)
                {
                case Token.LITERAL1:
                case Token.LITERAL2:
                case Token.LABEL:
                        addToken(mlength - lastOffset,Token.INVALID);
                        token = Token.NULL;
                        break;
                case Token.KEYWORD2:
                        addToken(mlength - lastOffset,token);
                        if (!backslash) token = Token.NULL;
                        addToken(mlength - lastOffset,token);
                        break;
                /*case Token.KEYWORD3:
                		if(keywords.lookup(line,lastOffset,mlength-lastOffset-1) == Token.KEYWORD3){
                			addToken(mlength - lastOffset,Token.KEYWORD3);
                    		addToken(mlength - lastOffset,Token.NULL);
                		}
                		addToken(mlength - lastOffset,Token.NULL);
                		token = Token.NULL;
                        break;*/
                default:
                        addToken(mlength - lastOffset,token);
                        break;
                }

                return token;
        }

        public static KeywordMap getKeywords()
        {
                return cKeywords;
        }

        // private members
        private static KeywordMap cKeywords;

        private KeywordMap keywords;
        private int lastOffset;
        private int lastKeyword;

        private boolean doKeyword(Segment line, int i, char c)
        {
                int i1 = i+1;

                int len = i - lastKeyword;
                byte id = keywords.lookup(line,lastKeyword,len);
                if(id != Token.NULL)
                {
                        if(lastKeyword != lastOffset)
                                addToken(lastKeyword - lastOffset,Token.NULL);
                        addToken(len,id);
                        lastOffset = i;
                }
                lastKeyword = i1;
                return false;
        }
}