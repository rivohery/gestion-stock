# 📦 Gestion de Stock & Vente (Fullstack)

Une application web robuste de gestion commerciale permettant de piloter les stocks, les ventes et les commandes fournisseurs en temps réel.

---

## 🚀 Technologies Utilisées

### **Backend (Spring Boot 3)**
* **Sécurité :** Spring Security 6, JWT (JSON Web Token) & Authentication.
* **Persistence :** Spring Data JPA, PostgreSQL (Dev), H2 (Tests).
* **Qualité :** Junit 5, Mockito, Spring Validation.
* **Documentation :** OpenAPI 3 & Swagger UI.
* **Architecture :** Monolithique (Vertical Slicing / Architecture verticale).

### **Frontend (Angular 19)**
* **Architecture :** Standalone Components.
* **State Management :** **NgRx** & **Signals** (Réactivité moderne).
* **UI/UX :** Tailwind CSS & Angular Material.
* **Features :** Gestion des formulaires réactifs, Intercepteurs JWT.

---

## ✨ Fonctionnalités Clés

### 🔐 Authentification & Sécurité
* Système complet de Login/Logout.
* Gestion des tokens (Refresh Token).
* Mot de passe oublié avec **envoi d'email** et token d'activation.
* Changement de mot de passe sécurisé.

### 🛠️ Gestion des Entités
* **CRUD complet :** Catégories, Produits, Fournisseurs et Utilisateurs.
* **Fonctions avancées :** Filtrage dynamique, Pagination côté serveur.
* **Upload :** Gestion des images et fichiers par l'utilisateur.

### 📉 Stock & Commercial
* **Stocks :** Suivi en temps réel, recherche avancée, activation/désactivation de produits.
* **Commandes Fournisseurs :** Flux complet de création, consultation et **génération de factures**.
* **Ventes :** Tunnel de vente, historique détaillé et facturation.

### 📊 Reporting & Exports
* **Dashboard :** Tableau de bord avec indicateurs clés de performance (KPI).
* **Exports multi-formats :** Génération de rapports en **PDF, CSV et Excel**.

---

## 🛠️ Installation et Lancement

### Prérequis
* JDK 17+
* Node.js 20+ & Angular CLI
* PostgreSQL

### Backend
1. Naviguez dans le dossier `backend/`
2. Configurez votre `application-dev.yml` avec vos identifiants PostgreSQL.
3. Lancez l'application :
   ```bash
   ./mvnw spring-boot:run

