﻿//------------------------------------------------------------------------------
// <auto-generated>
//    This code was generated from a template.
//
//    Manual changes to this file may cause unexpected behavior in your application.
//    Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace DataMigration
{
    using System;
    using System.Data.Entity;
    using System.Data.Entity.Infrastructure;
    
    public partial class fmsEntities1 : DbContext
    {
        public fmsEntities1()
            : base("name=fmsEntities1")
        {
        }
    
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            throw new UnintentionalCodeFirstException();
        }
    
        public DbSet<configuration> configurations { get; set; }
        public DbSet<quotation_autocomplete> quotation_autocomplete { get; set; }
        public DbSet<user> users { get; set; }
        public DbSet<quotation> quotations { get; set; }
        public DbSet<quotation_default> quotation_default { get; set; }
        public DbSet<quotations_history> quotations_history { get; set; }
    }
}
