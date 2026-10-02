# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = 'Sirop'
copyright = '2026, Louis Hildebrand'
author = 'Louis Hildebrand'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = ['myst_parser']

templates_path = ['_templates']
exclude_patterns = []

nitpicky = True

linkcheck_ignore = [
    # The ACM digital library returns 403 Forbidden when linkcheck tests these links.
    # I'm pretty sure DOI links will always be fine (that's the whole point of DOIs),
    # so just skip them.
    'https://doi.org/10.1145/3315454.3329957',
    'https://doi.org/10.1145/3385412.3385983',
    'https://doi.org/10.1145/3501768',
    'https://doi.org/10.1145/3814943.3816175',
]

# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = 'sphinx_book_theme'
html_theme_options = {
    'repository_url': 'https://github.com/louis-hildebrand/sirop-hls',
    'use_repository_button': True,
}
html_static_path = ['_static']
