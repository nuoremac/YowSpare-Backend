--
-- PostgreSQL database dump
--

-- Dumped from database version 15.5 (Ubuntu 15.5-0ubuntu0.23.04.1)
-- Dumped by pg_dump version 15.5 (Ubuntu 15.5-0ubuntu0.23.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: clients; Type: TABLE; Schema: public; Owner: tiers_user
--

CREATE TABLE public.clients (
    plafond_credit numeric(38,2),
    id bigint NOT NULL,
    canal_aquisition character varying(255),
    segment character varying(255),
    CONSTRAINT clients_canal_aquisition_check CHECK (((canal_aquisition)::text = ANY ((ARRAY['WEB'::character varying, 'RESEAU'::character varying, 'RECOMMANDATION'::character varying])::text[]))),
    CONSTRAINT clients_segment_check CHECK (((segment)::text = ANY ((ARRAY['PARTICULIER'::character varying, 'ENTREPRISE'::character varying, 'REVENDEUR'::character varying])::text[])))
);


ALTER TABLE public.clients OWNER TO tiers_user;

--
-- Name: commerciaux; Type: TABLE; Schema: public; Owner: tiers_user
--

CREATE TABLE public.commerciaux (
    commission numeric(38,2),
    date_debut_contrat date,
    date_fin_contrat date,
    id bigint NOT NULL,
    specialisations character varying(255),
    type_commercial character varying(255),
    zones_couvertes character varying(255),
    CONSTRAINT commerciaux_specialisations_check CHECK (((specialisations)::text = ANY ((ARRAY['B2B'::character varying, 'B2C'::character varying, 'SECTEUR_PUBLIC'::character varying, 'GRANDS_COMPTES'::character varying])::text[]))),
    CONSTRAINT commerciaux_type_commercial_check CHECK (((type_commercial)::text = ANY ((ARRAY['INTERNE'::character varying, 'EXTERNE'::character varying, 'INDEPENDANT'::character varying])::text[]))),
    CONSTRAINT commerciaux_zones_couvertes_check CHECK (((zones_couvertes)::text = ANY ((ARRAY['NORD'::character varying, 'SUD'::character varying, 'EST'::character varying, 'OUEST'::character varying, 'INTERNATIONAL'::character varying])::text[])))
);


ALTER TABLE public.commerciaux OWNER TO tiers_user;

--
-- Name: fournisseurs; Type: TABLE; Schema: public; Owner: tiers_user
--

CREATE TABLE public.fournisseurs (
    id bigint NOT NULL,
    certification character varying(255),
    delai_livraison character varying(255),
    mode_paiement character varying(255),
    produits_principaux character varying(255),
    CONSTRAINT fournisseurs_mode_paiement_check CHECK (((mode_paiement)::text = ANY ((ARRAY['VIREMENT'::character varying, 'CHEQUE'::character varying, 'TRAITE'::character varying])::text[]))),
    CONSTRAINT fournisseurs_produits_principaux_check CHECK (((produits_principaux)::text = ANY ((ARRAY['ELECTRONIQUE'::character varying, 'MATERIEL'::character varying, 'LOGICIEL'::character varying])::text[])))
);


ALTER TABLE public.fournisseurs OWNER TO tiers_user;

--
-- Name: prospects; Type: TABLE; Schema: public; Owner: tiers_user
--

CREATE TABLE public.prospects (
    date_conversion date,
    probabilite integer,
    id bigint NOT NULL,
    notes_prospect character varying(2000),
    potentiel character varying(255),
    source_prospect character varying(255),
    CONSTRAINT prospects_potentiel_check CHECK (((potentiel)::text = ANY ((ARRAY['FAIBLE'::character varying, 'MOYEN'::character varying, 'ELEVE'::character varying, 'STRATEGIQUE'::character varying])::text[]))),
    CONSTRAINT prospects_source_prospect_check CHECK (((source_prospect)::text = ANY ((ARRAY['SITE_WEB'::character varying, 'RESEAU_SOCIAL'::character varying, 'SALON'::character varying, 'RECOMMANDATION'::character varying])::text[])))
);


ALTER TABLE public.prospects OWNER TO tiers_user;

--
-- Name: tiers; Type: TABLE; Schema: public; Owner: tiers_user
--

CREATE TABLE public.tiers (
    date_creation date,
    created_at timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_at timestamp(6) without time zone,
    description character varying(1000),
    address character varying(255),
    canal_prefere character varying(255),
    city character varying(255),
    complement character varying(255),
    email character varying(255),
    long_name character varying(255),
    name character varying(255) NOT NULL,
    numero_fiscal character varying(255),
    pays character varying(255),
    phone_number character varying(255),
    postal_code character varying(255),
    registre_commerce character varying(255),
    secteur_activite character varying(255),
    short_name character varying(255),
    taille_entreprise character varying(255),
    type_entreprise character varying(255),
    website character varying(255),
    CONSTRAINT tiers_canal_prefere_check CHECK (((canal_prefere)::text = ANY ((ARRAY['EMAIL'::character varying, 'PHONE'::character varying, 'COURRIER'::character varying, 'IN_PERSON'::character varying])::text[]))),
    CONSTRAINT tiers_pays_check CHECK (((pays)::text = ANY ((ARRAY['CMR'::character varying, 'CG'::character varying, 'TC'::character varying, 'GB'::character varying, 'CI'::character varying])::text[]))),
    CONSTRAINT tiers_secteur_activite_check CHECK (((secteur_activite)::text = ANY ((ARRAY['IT'::character varying, 'FINANCE'::character varying, 'SANTE'::character varying, 'INDUSTRIE'::character varying, 'COMMERCE'::character varying])::text[]))),
    CONSTRAINT tiers_taille_entreprise_check CHECK (((taille_entreprise)::text = ANY ((ARRAY['MICRO'::character varying, 'PME'::character varying, 'ETI'::character varying, 'GE'::character varying])::text[]))),
    CONSTRAINT tiers_type_entreprise_check CHECK (((type_entreprise)::text = ANY ((ARRAY['PARTICULIER'::character varying, 'ENTREPRISE'::character varying, 'REVENDEUR'::character varying])::text[])))
);


ALTER TABLE public.tiers OWNER TO tiers_user;

--
-- Name: tiers_id_seq; Type: SEQUENCE; Schema: public; Owner: tiers_user
--

CREATE SEQUENCE public.tiers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tiers_id_seq OWNER TO tiers_user;

--
-- Name: tiers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tiers_user
--

ALTER SEQUENCE public.tiers_id_seq OWNED BY public.tiers.id;


--
-- Name: tiers id; Type: DEFAULT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.tiers ALTER COLUMN id SET DEFAULT nextval('public.tiers_id_seq'::regclass);


--
-- Name: clients clients_pkey; Type: CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);


--
-- Name: commerciaux commerciaux_pkey; Type: CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.commerciaux
    ADD CONSTRAINT commerciaux_pkey PRIMARY KEY (id);


--
-- Name: fournisseurs fournisseurs_pkey; Type: CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.fournisseurs
    ADD CONSTRAINT fournisseurs_pkey PRIMARY KEY (id);


--
-- Name: prospects prospects_pkey; Type: CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.prospects
    ADD CONSTRAINT prospects_pkey PRIMARY KEY (id);


--
-- Name: tiers tiers_pkey; Type: CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.tiers
    ADD CONSTRAINT tiers_pkey PRIMARY KEY (id);


--
-- Name: prospects fk1360knu15stp4501f5qj4d76i; Type: FK CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.prospects
    ADD CONSTRAINT fk1360knu15stp4501f5qj4d76i FOREIGN KEY (id) REFERENCES public.tiers(id);


--
-- Name: fournisseurs fk6kqaodkqep4swnqqp0mbd1c5p; Type: FK CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.fournisseurs
    ADD CONSTRAINT fk6kqaodkqep4swnqqp0mbd1c5p FOREIGN KEY (id) REFERENCES public.tiers(id);


--
-- Name: clients fk7rqecevkrnv6lob43sf990riv; Type: FK CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT fk7rqecevkrnv6lob43sf990riv FOREIGN KEY (id) REFERENCES public.tiers(id);


--
-- Name: commerciaux fkc98kkacioxhlxj6mautihmpwp; Type: FK CONSTRAINT; Schema: public; Owner: tiers_user
--

ALTER TABLE ONLY public.commerciaux
    ADD CONSTRAINT fkc98kkacioxhlxj6mautihmpwp FOREIGN KEY (id) REFERENCES public.tiers(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT ALL ON SCHEMA public TO tiers_user;


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES  TO tiers_user;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES  TO tiers_user;


--
-- PostgreSQL database dump complete
--

