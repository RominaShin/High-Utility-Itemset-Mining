using BenchmarkDotNet.Running;
using System;
using TKO_CSharp.Benchmark;
using TKO_CSharp.Models;

namespace TKO_CSharp
{
    class Program
    {
        static void Main(string[] args)
        {
            var summary = BenchmarkRunner.Run<TKO_AlgorithmBenchmarks>();
        }
    }
}
