using System;
using TKO_CSharp.Models;
namespace TKO_CSharp
{
    class Program
    {
        static void Main(string[] args)
        {
            TKO_Algorithm algorithm = new TKO_Algorithm();
            string input = @"E:\Education\Final Project\tko\tko\DB_Utility.txt";
            string output = @"E:\Education\Final Project\tko\tko\result.txt";
            algorithm.RunAlgorithm(input, output, 2);
            algorithm.WriteResultTofile(output);
            algorithm.PrintStats();
            Console.WriteLine("Hello World!");
        }
    }
}
